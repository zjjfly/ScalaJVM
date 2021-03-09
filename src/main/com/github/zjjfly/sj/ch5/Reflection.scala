package com.github.zjjfly.sj.ch5

import java.lang.annotation.Annotation
import java.lang.reflect._

/**
 * @author zjjfly[https://github.com/zjjfly]
 * @date 2021/2/11
 */
object Reflection extends App {

  //获取Class
  println(classOf[String])

  //获取类的方法
  println(classOf[String].getDeclaredMethods.mkString(","))

  //判断是否是某个类的对象
  assert(classOf[String].isInstance("string"))

  assert(!classOf[String].isInstance(1))

  def combineIfPossible(clazz: Class[_]): Option[(Any, Any) => Option[Any]] = {
    val objOk: Any => Boolean = obj => clazz.isInstance(obj)
    val combine: Option[Method] = clazz.getDeclaredMethods.find { m: Method =>
      m.getParameterTypes.toList == List(clazz) && m.getReturnType == clazz
    }
    combine.map { m: Method =>
      (obj1: Any, obj2: Any) =>
        // invoke: first obj is this, rest are args of method
        if (objOk(obj1) && objOk(obj2)) Some(m.invoke(obj1, obj2.asInstanceOf[AnyRef]))
        else None
    }
  }

  println(combineIfPossible(classOf[String]).flatMap(_ ("a", "b")))

  println(combineIfPossible(classOf[String]).flatMap(_ ("a", 1)))

  //其他的获取到Class的方式
  println(combineIfPossible(Class.forName("java.lang.String")).flatMap(_ ("a", "b")))

  //通过ClassLoader获取接口的所有实现类

  import scala.jdk.CollectionConverters._ // 2.13
  trait MyInterface

  class Implementation1 extends MyInterface

  class Implementation2 extends MyInterface

  def getImplementations(interface: Class[_]) = {
    val classes: Field = classOf[ClassLoader].getDeclaredField("classes")
    classes.setAccessible(true) // field classes is protected
    val all = classes.get(interface.getClassLoader)

    def isImplementation(clazz: Class[_]) = try {
      lazy val implementsInterface =
        clazz.getGenericInterfaces.contains(classOf[MyInterface])
      lazy val extendsClass =
        clazz.getGenericSuperclass == classOf[MyInterface]
      implementsInterface || extendsClass
    } catch {
      // if we are running this for all Classes, some might be
      // parametric and complain with MalformedParameterizedTypeException
      case _: Throwable => false
    }

    all.asInstanceOf[java.util.Vector[Class[_]]]
       .clone.asInstanceOf[java.util.Vector[Class[_]]]
       .asScala
       .filter(isImplementation)
  }

  //在使用Implementation1或Implementation2之前
  println(getImplementations(classOf[MyInterface]))
  //ArrayBuffer()

  //使用过Implementation1和Implementation2之后
  new Implementation1
  new Implementation2
  println(getImplementations(classOf[MyInterface]))
  //ArrayBuffer(class com.github.zjjfly.sj.ch5.Reflection$Implementation1, class com.github.zjjfly.sj.ch5.Reflection$Implementation2)

  //通过反射获取类上面的注解
  //书里面有错误,getAnnotations得到的数组中元素类型是一个随机的class com.sun.proxy.$Proxy1对象
  //使用Annotation的annotationType方法才能得到正确的类型,详见https://errorprone.info/bugpattern/GetClassOnAnnotation
  assert(classOf[MyClass].getAnnotations.map((annotation: Annotation) => annotation.annotationType())
                         .contains(classOf[MyAnnotation]))


  //应该尽量避免使用反射,因为它容易引发bug,并且有性能问题.对于Scala,可以使用一些编译时反射的库,如Circe(https://circe.github.io/circe/)
  //或者使用一些基于macro的库,如Chimney(https://github.com/scalalandio/chimney)
}

//关于scala编译时反射
//1.scala编译器知道所有可以知道的类型信息,所以它可以判断方法某个参数的类型是否匹配
//2.shapeless这样的库或derives关键字(Dotty)可以让我们基于现存的较小问题的实现组成一个较大问题的实现
//3.使用macro可以基于其所处位置可获得的所有信息生成任意代码
//所以Scala可以减少90%的使用,剩下的10%推荐使用Scala的反射API

//作者更推荐使用TypeTag,相比于ClassTag或Class[_]
object ScalaReflectionApi extends App {

  import scala.reflect.runtime.universe._

  //使用Scala的反射api获取伴生对象
  def findCompanionOf[T: TypeTag] = runtimeMirror(getClass.getClassLoader)
    .reflectModule(typeOf[T].typeSymbol.companion.asModule).instance

  //可以支持primitive type
  println(findCompanionOf[Long])
  //object scala.Long

  //使用Java的反射来获取伴生对象
  def findCompanionOf(clazz: Class[_]) = clazz.getClassLoader
                                              .loadClass(clazz.getName + "$")
                                              .getField("MODULE$")
                                              .get(null)

  println(findCompanionOf(classOf[List[_]]))
  //scala.collection.immutable.List$@535779e4

  //对于primitive type会报错
  //println(findCompanionOf(classOf[Long]))
  //Exception in thread "main" java.lang.NullPointerException

  //对于Java标准库中的类型也会报错,因为Java中没有伴生对象的概念
  //findCompanionOf(classOf[java.lang.Long])
  //Exception in thread "main" java.lang.NullPointerException

  //Scala的反射API的限制是,无法在运行时确定某个值是否是某个类型的实例,只能使用Java的反射API

  import scala.reflect.{ClassTag, classTag}

  def withTypeParam[T: ClassTag]: Any => String = {
    case t: T if classTag[T].runtimeClass.isInstance(t) => "T matched"
    case _ => "T not matched"
  }

  println(withTypeParam[String].apply("ss"))
  //T matched
  println(withTypeParam[String].apply(1))
  //T not matched

}

