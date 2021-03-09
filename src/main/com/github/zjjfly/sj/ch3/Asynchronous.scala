package com.github.zjjfly.sj.ch3

import cats.effect.{ExitCode, IO, IOApp}
import monix.eval.{Task, TaskApp}

import java.util.concurrent.TimeoutException
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

/**
 * @author zjjfly[https://github.com/zjjfly] on 2021/1/28
 */
//使用Scala默认的Future来实现异步任务
object Asynchronous extends App {
  Future {
    //这个打印不会执行,因为程序马上会结束而不是等待Future中的代码执行完毕
    //根本原因是Future使用的ExecutionContext是基于线程池的,且其中的线程都是daemon,而JVM在只有daemon线程的时候是会终止的
    println("Hello")
  }
}

//monix的TaskApp实现的异步任务,它会在任务执行完成之后再结束
object MonixTask extends TaskApp {
  override def run(args: List[String]): Task[ExitCode] = {
    Task {
      java.lang.Thread.sleep(1000L)
      println("Success!")
      ExitCode.Success
    }
  }
}

//cats-effect的IOApp实现的异步任务,它会在任务执行完成之后再结束
object IOMonad extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    //    val value = IO.race(IO.delay {
    //      1 to 100 reduce (_ + _)
    //    }, IO.delay {
    //      2 to 150 reduce (_ + _)
    //    })
    //    value.map {
    //      case Right(n) =>
    //        println(s"right:$n")
    //        ExitCode.Success
    //      case Left(n) =>
    //        println(s"left:$n")
    //        ExitCode.Error
    //    }
    IO.delay {
      java.lang.Thread.sleep(1000L)
      println("Success!")
      ExitCode.Success
    }
  }
}

object ThreadInterrupt extends App {
  //使用While这种无限循环的线程,无法保证被interrupt停止
  val task = Task {
    while (true) {
      java.lang.Thread.sleep(1000L)
      println("cannot be killed")
    }
  }

  import monix.execution.Scheduler.Implicits.global

  val cancelable = task.runAsync {
    case Left(value) =>
    case Right(value) =>
  }
  java.lang.Thread.sleep(1000L)
  cancelable.cancel()
}

//相比上面的方式,下面这种可以做到对Task的停止
object TaskInterrupt extends App {

  import monix.execution.Scheduler.Implicits.global

  //使用Task的flatMap和递归的方式,每一次执行的Task中的业务代码,在底层都是不同的指令
  val task: Task[_] = Task(println("can be killed"))
    .flatMap(_ => task)
  val cancelable = task.runAsync {
    case Left(_) =>
    case Right(_) =>
  }
  java.lang.Thread.sleep(2000L)
  cancelable.cancel()
}

//Await的result方法在超时后虽然会抛出异常,但不意味着里面的Future停止了,只是Await不再等待它完成了
object AwaitTimeout extends App {

  import scala.concurrent.duration._

  try {
    Await.result(Future {
      java.lang.Thread.sleep(2000L)
      println("I'm not dead")
    }, 1 seconds)
  } catch {
    case e: TimeoutException => assert(true)
    case _ => assert(false)
  }
  java.lang.Thread.sleep(1000L)
}
