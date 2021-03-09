package com.github.zjjfly.sj.ch2

/**
 * @author zjjfly[https://github.com/zjjfly] on 2021/1/18
 */
package x {

  class Access {
    private def privateMethod() = "I am private"

    //Scala的protected和Java的不同的一点是,它无法通过同一个package中的类访问
    protected def protectedMethod() = "I am protected"

    def publicMethod() = "I am public"

    println("Access")
    println(privateMethod)
    println(protectedMethod)
    println(publicMethod)
  }

  class SamePackage {
    val access = new Access
    println("SamePackage")
    // println(access.privateMethod) // outside cannot access it
    // println(access.protectedMethod) // same package cannot access it
    println(access.publicMethod) // always available
  }

}

package y {

  class AccessSubclass extends x.Access {
    println("AccessSubclass")
    // println(privateMethod) // outside cannot access it
    println(protectedMethod) // subclass can access it
    println(publicMethod) // always available
  }

  object AccessTest {

    def main(args: Array[String]): Unit = {
      println("creating Access")
      new x.Access
      println()
      println("creating SamePackage")
      new x.SamePackage
      println()
      println("creating AccessSubclass")
      new y.AccessSubclass
    }
  }

}
