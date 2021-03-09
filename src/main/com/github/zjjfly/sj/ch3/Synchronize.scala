package com.github.zjjfly.sj.ch3

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * @author zjjfly[https://github.com/zjjfly]
 * @since 2021/2/5
 */
case class Item(name: String)

//下面类中的swapItems和addToAll在多线程环境都会出现问题
//class Buckets {
//  private val buckets = Array.ofDim[Set[Item]](10)
//  for (i <- buckets.indices)
//    buckets(i) = Set(Item("item " + i))
//
//  def swapItems(idx1: Int, idx2: Int): Unit = {
//    if (idx1 < buckets.length && idx2 < buckets.length) {
//      val tmp = buckets(idx1)
//      buckets(idx1) = buckets(idx2)
//      buckets(idx2) = tmp
//    }
//  }
//
//  def addToAll(item: Item): Unit = for (i <- buckets.indices)
//    buckets(i) = (buckets(i) + item)
//}

//使用JVM自带的synchronized关键字解决多线程问题
class Buckets {
  private val buckets = Array.ofDim[Set[Item]](10)
  for (i <- buckets.indices)
    buckets(i) = Set(Item("item " + i))

  def swapItems(idx1: Int, idx2: Int): Unit = synchronized {
    if (idx1 < buckets.length && idx2 < buckets.length) {
      val tmp = buckets(idx1)
      buckets(idx1) = buckets(idx2)
      buckets(idx2) = tmp
    }
  }

  def addToAll(item: Item): Unit = synchronized {
    for (i <- buckets.indices)
      buckets(i) = (buckets(i) + item)
  }
}

//synchronized的问题是容易产生死锁,例如下面这样的代码:
object DeadLock extends App {
  val a = new Object
  val b = new Object

  def thread1Run(): Unit = a.synchronized {
    b.synchronized {
      println("hello 1")
    }
  }

  def thread2Run(): Unit = b.synchronized {
    a.synchronized {
      println("hello 2")
    }
  }

  Future {
    thread1Run()
  }
  Future {
    thread2Run()
  }
  Thread.currentThread().join(3000L)
}

//多线程的另一个问题是可见性,因为现代cpu是多核的,每个核都有自己的二级三级缓存,这些缓存的数据是主存中的数据的拷贝
//当一个核更新数据后,其他核如果有这个值的缓存,那么就会有数据不一致问题,例子如下:
//object Names extends App {
//
//  var counter = 0
//
//  new Thread(() => {
//    while (counter < 1010) {
//      println {
//        counter = counter + 1
//        counter
//      }
//      Thread.sleep(50)
//    }
//  }).start()
//
//  new Thread(() => {
//    var previous = counter
//    while (previous < 1010) {
//      if (counter != previous) {
//        previous = counter
//        println {
//          if (previous < 10) "a few"
//          else if (previous < 100) "tens"
//          else if (previous < 1000) "hundreds"
//          else s"a lot: $counter"
//        }
//      }
//    }
//  }).start()
//}

// 上面两个线程很大概率会在不同的核中跑,因为其中一个线程会不时的sleep,另一个始终在运行,把它们放在不同的核心可以有更好的性能
// 如果在上面的第二个线程中加上Thread.sleep(50),那么它们很大可能会在同一个核心
object Names extends App {
  var counter = 0
  new Thread(() => {
    while (counter < 1010) {
      println {
        counter = counter + 1
        counter
      }
      Thread.sleep(50)
    }
  }).start()
  new Thread(() => {
    var previous = counter
    while (previous < 1010) {
      if (counter != previous) {
        previous = counter
        println {
          if (previous < 10) "a few"
          else if (previous < 100) "tens"
          else if (previous < 1000) "hundreds"
          else s"a lot: $counter"
        }
      }
      Thread.sleep(50)
    }
  }).start()
}

//但不能靠sleep来控制,因为这不是CPU的确定性行为,所以更好的做法是告诉计算机不要使用CPU缓存
//这个可以通过JVM的volatile关键字完成,在Scala中是@volatile注解
object Names2 extends App {
  @volatile var counter = 0
  new Thread(() => {
    while (counter < 1010) {
      println {
        counter = counter + 1
        counter
      }
      Thread.sleep(100)
    }
  }).start()
  new Thread(() => {
    var previous = counter
    while (previous < 1010) {
      if (counter != previous) {
        previous = counter
        println {
          if (previous < 10) "a few"
          else if (previous < 100) "tens"
          else if (previous < 1000) "hundreds"
          else s"a lot: $counter"
        }
      }
    }
  }).start()
}

//输出:
//a few
//1
//2
//a few
//a few
//可以看出输出结果会有先打印a few后打印counter值的情况,可以使用synchronized避免
object Names3 extends App {
  @volatile var counter = 0
  new Thread(() => {
    while (counter < 1010) {
      Names3.synchronized {
        println {
          counter = counter + 1
          counter
        }
      }
      Thread.sleep(100)
    }
  }).start()
  new Thread(() => {
    var current = Names.synchronized {
      counter
    }
    var previous = counter
    while (previous < 1010) {
      if (Names3.synchronized {
        current = counter
        current
      } != previous) {
        previous = current
        println {
          if (previous < 10) "a few"
          else if (previous < 100) "tens"
          else if (previous < 1000) "hundreds"
          else s"a lot: $counter"
        }
      }
    }
  }).start()
}
