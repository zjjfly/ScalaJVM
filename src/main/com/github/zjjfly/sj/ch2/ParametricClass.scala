package com.github.zjjfly.sj.ch2

/**
 * @author zjjfly[https://github.com/zjjfly] on 2021/1/18
 */
sealed trait MyList[+T]

case object Nil extends MyList[Nothing]

case class Cons[@specialized(Int) T](head: T, tail: MyList[T]) extends MyList[T]
