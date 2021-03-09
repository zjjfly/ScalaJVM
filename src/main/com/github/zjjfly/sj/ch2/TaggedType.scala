package com.github.zjjfly.sj.ch2

import com.github.zjjfly.sj.ch2.TaggedType.tag
import com.github.zjjfly.sj.ch2.TaggedType.tag.@@

/**
 * @author zjjfly[https://github.com/zjjfly] on 2021/1/18
 */
object TaggedType {

  object tag {

    def apply[U]: Tagger[U] = Tagger.asInstanceOf[Tagger[U]]

    trait Tagged[U] extends Any

    type @@[+T, U] = T with Tagged[U]

    class Tagger[U] {
      def apply[T](t: T): T @@ U = t.asInstanceOf[T @@ U]
    }

    private object Tagger extends Tagger[Nothing]

  }

}

object Age {

  sealed trait AgeTag

  type Age = Int @@ AgeTag

  def apply(int: Int): Age = tag[AgeTag][Int](int)

}
