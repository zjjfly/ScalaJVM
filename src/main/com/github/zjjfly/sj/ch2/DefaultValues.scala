package com.github.zjjfly.sj.ch2

/**
 * @author zjjfly[https://github.com/zjjfly] on 2021/1/15
 */
object DefaultValues {

  def method(i: Int = 0, s: String = ""): Unit = println(s"$i $s")

  def main(args: Array[String]): Unit = {
    method()
  }
}
