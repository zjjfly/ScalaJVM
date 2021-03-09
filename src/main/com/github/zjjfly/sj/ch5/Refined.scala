package com.github.zjjfly.sj.ch5

/**
 * scala中会产生反射代码的例子
 *
 * @author zjjfly[https://github.com/zjjfly]
 * @date 2021/2/27
 */
trait MyTrait

//refine type生成的代码会使用反射
class Refined {
  def takeRefined(in: MyTrait {def someMethod(): Unit}): Unit = in.someMethod()
}
