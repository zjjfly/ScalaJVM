package com.github.zjjfly.sj.ch4

/**
 * 虽然有GC,但JVM中的还是会有内存泄露问题
 *
 * @author zjjfly[https://github.com/zjjfly]
 * @date 2021/2/10
 */
object MemoryLeak extends App {

  //这个类存放了对它的上一个关联的对象的引用,最终形成的是一个对象链,只要其中一个对象无法被回收,整个链也是无法被回收的
  class Result(private val value: String,
               private val previousResult: Option[Result] = None) {

    val computed: String = value ++
      previousResult.map(_.computed).getOrElse("")

    def nextResult(newValue: String): Result = new Result(newValue, Some(this))
  }

  (0 until 10).foldLeft(new Result("")) { (acc, i) =>
    acc.nextResult(i.toString)
  }

}

//lambda大多数情况下会被编译为类中的静态方法,然后使用invokedynamic指令调用这个方法
object AnonymousClass {
  private val name = "test"
  List(1, 2, 3, 4, 5).map { i =>
    println(i + name)
  }
}

sealed trait MaybeLeaking extends Product with Serializable

//下面的例子中,如果用户一直输入除了stop和clean之外的字符串,堆内存中会有很多Leaking没办法被回收,可以使用VisualVM观察
object MaybeLeaking {

  case object NotLeaking extends MaybeLeaking

  final case class Leaking(previous: MaybeLeaking) extends MaybeLeaking

  def main(args: Array[String]): Unit = {
    @scala.annotation.tailrec
    def iterate(previous: MaybeLeaking): Unit = {
      println(previous)
      Option(scala.io.StdIn.readLine()).getOrElse("stop").trim.toLowerCase
      match {
        case "stop" => ()
        case "clean" => iterate(NotLeaking)
        case _ => iterate(Leaking(previous))
      }
    }

    iterate(NotLeaking)
  }
}
