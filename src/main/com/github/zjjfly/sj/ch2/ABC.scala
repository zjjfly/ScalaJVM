package com.github.zjjfly.sj.ch2

/**
 * @author zjjfly[https://github.com/zjjfly] on 2021/1/18
 */
trait A {
  def doSomething(): Unit
}

trait B1 extends A {
  override def doSomething(): Unit = println("I am B1")
}

trait B2 extends A {
  override def doSomething(): Unit = println("I am B2")
}

class C extends B1 with B2
