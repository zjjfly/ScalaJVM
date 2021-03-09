package com.github.zjjfly.sj.ch4

/**
 *
 * @author zjjfly[https://github.com/zjjfly]
 * @date 2021/2/10
 */
//在JVM中,原始类型的值会被特殊处理,对于它们的包装类型也会做一些缓存
object Value extends App {

  def areEq(a: Integer, b: Integer) = a eq b

  println(areEq(1024, 1024))
  //false

  println(areEq(124, 124))
  //true
}

//String字面量也会有缓存
object Strings extends App {

  println("" eq "")
  //true

  println(new String("") eq new String(""))
  //false

  //如果字符串对象需要缓存,可以使用intern方法
  val str: String = new String("foo").intern()
  println(str eq "foo")
  //true
}
