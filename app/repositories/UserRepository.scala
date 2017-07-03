package repositories

import play.api.libs.json._
import reactivemongo.api.commands.{DefaultWriteResult, WriteError, WriteResult}

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.ReadPreference
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.collection.JSONCollection
import play.modules.reactivemongo.json._

import scala.concurrent.ExecutionContext.Implicits.global
import models.User
import org.mindrot.jbcrypt.BCrypt

/**
  * Created by chenshijue on 2017/6/30.
  */

class UserRepository(collection: JSONCollection) extends Repository[User] {

    override def add(item: User)(implicit ec: ExecutionContext): Future[(WriteResult, User)] = {
        if (item.name == None || item.password == None || item.mobile == None){
            return Future(DefaultWriteResult(false, 0, Nil, None, None, Some("Parameter error")), null)
        }
        val encrypPassword = BCrypt.hashpw(item.password.getOrElse(""), BCrypt.gensalt())
        item._id match {
            case Some(oid) => {
                item.password = Some(encrypPassword)
            }
            case None => {
                item._id = Some(BSONObjectID.generate.stringify)
                item.password = Some(encrypPassword)
            }
        }

        collection.update(Json.obj("_id" -> item._id), item, upsert = true).map(result => (result, item))
    }

    override def remove(selector: JsObject)(implicit ec: ExecutionContext): Future[WriteResult] =
        collection.remove(selector)

    override def removeAll()(implicit ec: ExecutionContext): Future[WriteResult] = collection.remove(Json.obj())

    override def update(item: User)(implicit ec: ExecutionContext): Future[WriteResult] = {
        item._id match {
            case Some(oid) => {
                val encrypPassword = BCrypt.hashpw(item.password.getOrElse(""), BCrypt.gensalt())
                item.password = Some(encrypPassword)
            }
            case None => {
                return Future(DefaultWriteResult(false, 0, Nil, None, None, Some("Parameter error")))
            }
        }

        collection.update(Json.obj("_id" -> item._id), Json.obj("$set" -> item))
    }

    override def findOne(selector: JsObject)(implicit ec: ExecutionContext): Future[Option[JsObject]] =
        collection.find(selector).one[JsObject]

    override def find(selector: JsObject)(implicit ec: ExecutionContext): Future[List[JsObject]] =
        collection.find(selector).cursor[JsObject](ReadPreference.primary).collect[List]()

}
