package com.github.zjjfly.sj.ch1

/**
 * @author zjjfly[https://github.com/zjjfly] on 2021/1/15
 */
object Exceptional {

  def main(args: Array[String]): Unit = {
    val numbers = Array(4, 2, 0)
    try {
      numbers.foreach(n => println(8 / n))
    } catch {
      case _: ArithmeticException =>
        println("Oops!")
    }
    throwNoStackTraceException()
  }

  def search(list: List[String]): Option[String] = {
    list.foreach { string =>
      if (string.nonEmpty) {
        // this actually throws stackless exception!
        return Some(string)
      }
    }
    None
    // somewhere here search catches exception and turns it into result
  }

  def throwNoStackTraceException() = throw new NoStackTraceRuntimeException()
}

//这种错误类型不会产生stack trace
class NoStackTraceRuntimeException extends RuntimeException {
  override def fillInStackTrace(): Throwable = this
}
