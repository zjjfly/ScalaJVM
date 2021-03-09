package com.github.zjjfly.sj.ch2

/**
 * @author zjjfly[https://github.com/zjjfly] on 2021/1/18
 */
class Merger[A](zero: A) {
  def merge(list: List[A])(f: (A, A) => A): A = list.fold(zero)(f)
}

class OverloadedMethods[A] {
  def apply(a: A): Unit = println("I'm A")

  def apply(b: String): Unit = println("I'm String")
}

object OverloadedMethods {
  def main(args: Array[String]): Unit = {
    new OverloadedMethods[String].apply(a = "test")
    val test = withTypeParam[String]
    println(test("test"))
    //T matched
    println(test(24))
    //T matched
    println(withTypeParam2[String].apply("test"))
    //T matched
    println(withTypeParam2[String].apply(12))
    //T not matched
    //ClassTag无法支持原始类型
    println(withTypeParam2[Int].apply(34))
    //T not matched
    println(withTypeParam2[Integer].apply(34))
    //T matched
  }

  def withTypeParam[T]: Any => String = {
    case _: T => "T matched"
    case _ => "T not matched"
  }

  import scala.reflect.{ClassTag, classTag}

  def withTypeParam2[T: ClassTag]: Any => String = {
    case t: T if classTag[T].runtimeClass.isInstance(t) => "T matched"
    case _ => "T not matched"
  }
}


