package models

import java.text.SimpleDateFormat

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import java.util.Date

import OWritesOps._

/**
  * Created by chenshijue on 2017/6/29.
  */

object UserRole {

    //普通用户
    val common = "common"

    //管理员
    val admin = "admin"

    def authCommonList = List(common, admin)

    def authAdminList = List(admin)
}

case class User(var _id: Option[String], name: Option[String], var password: Option[String], mobile: Option[String],
                email: Option[String]){
    var role = UserRole.common
    val created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date)
}

object User {

    implicit val userReads: Reads[User] = (
        (JsPath \ "_id").readNullable[String] and
            (JsPath \ "name").readNullable[String](minLength[String](4) keepAnd maxLength[String](20)) and
            (JsPath \ "password").readNullable[String](minLength[String](6) keepAnd maxLength[String](25)) and
            (JsPath \ "mobile").readNullable[String](minLength[String](4) keepAnd maxLength[String](20)) and
            (JsPath \ "email").readNullable[String](email)
    )(User.apply _)

    implicit val userOWrites: OWrites[User] = Json.writes[User].addField("role", _.role).
        addField("created", _.created)

}
