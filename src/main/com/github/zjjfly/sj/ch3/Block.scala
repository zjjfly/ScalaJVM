package com.github.zjjfly.sj.ch3

/**
 * 阻塞线程的一些方式
 *
 * @author zjjfly[https://github.com/zjjfly]
 * @since 2021/2/7
 */
object Sleep extends App {
  Thread.sleep(1000L)
}

object WaitNotify extends App {
  val notifier = new Object {} // every object can be used!
  new Thread(() => {
    println("Start waiting for the big event!")
    notifier.synchronized { // synchronized is required to wait!
      notifier.wait()
    }
    println("Big event happened!")
  }).start()
  Thread.sleep(2000L)
  notifier.synchronized { // synchronized is required to wait!
    notifier.notify()
  }
}
