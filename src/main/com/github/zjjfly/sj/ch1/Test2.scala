package com.github.zjjfly.sj.ch1

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * @author zjjfly[https://github.com/zjjfly] on 2021/1/15
 */
object Test2 {

  def main(args: Array[String]): Unit = println(loop2(100000, 0))

  def loop(int: Int): Int = if (int <= 0) 0 else 2 + loop(int - 1)

  @tailrec
  def loop2(int: Int, accumulator: Int): Int = if (int <= 0) accumulator else loop2(int - 1, accumulator + 2)

  //有些代码无法使用尾递归优化,例如Future和monix的Task
  def repeat[A](times: Int)(task: Future[A]): Future[A] =
    if (times <= 0) Future.failed(new Exception("times=0 is < 1!")) else if (times == 1) task
    else task.flatMap(_ => repeat(times - 1)(task))

}
