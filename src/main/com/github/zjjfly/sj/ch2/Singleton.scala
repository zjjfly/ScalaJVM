package com.github.zjjfly.sj.ch2

/**
 * @author zjjfly[https://github.com/zjjfly] on 2021/1/18
 */
object Singleton {
  def thisIsMethod(): Unit = println("hello")
}

trait DoSomeMagic {
  def magic(): Unit = println(":(){ :|: &};: # did you know that copy-pasting 􏰀→ random text to bash is a bad idea")
}

object ThisIsSingleton extends DoSomeMagic
