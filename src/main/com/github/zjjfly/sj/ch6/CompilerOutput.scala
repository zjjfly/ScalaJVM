package com.github.zjjfly.sj.ch6

/**
 * @author zjjfly[https://github.com/zjjfly]
 * @date 2021/3/1
 */
object CompilerOutput {
  def nextInRange(from: Int, to: Int): Int =
    scala.util.Random.nextInt(to - from) + from

  def sqrt(of: Int): Int =
    java.lang.Math.sqrt(of.toDouble).toInt

  def main(args: Array[String]): Unit = {
    var found = 0
    while (found < 1000) {
      val value = nextInRange(10, 1000)
      val valueSqrt = sqrt(value)
      val isSquare = valueSqrt * valueSqrt == value
      if (isSquare) {
        // you can uncomment to check it works
        println("Found: " + value + " is square of " + valueSqrt)
        found = found + 1
      }
    }
  }
}
