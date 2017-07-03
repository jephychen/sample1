package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by chenshijue on 2017/6/29.
  */

class OWritesOps[A](writes: OWrites[A]) {
    def addField[T: Writes](fieldName: String, field: A => T): OWrites[A] =
        (writes ~ (__ \ fieldName).write[T])((a: A) => (a, field(a)))


    def removeField(fieldName: String): OWrites[A] = OWrites { a: A =>
        val transformer = (__ \ fieldName).json.prune
        Json.toJson(a)(writes).validate(transformer).get
    }
}

object OWritesOps {
    implicit def from[A](writes: OWrites[A]): OWritesOps[A] = new OWritesOps(writes)
}
