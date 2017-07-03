package repositories

import play.api.libs.json._
import reactivemongo.api.commands.WriteResult

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.ReadPreference
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.collection.JSONCollection
import play.modules.reactivemongo.json._

import scala.concurrent.ExecutionContext.Implicits.global
import models.User

/**
  * Created by chenshijue on 2017/6/30.
  */
class UserRepository(collection: JSONCollection) extends Repository[User] {

    override def add(item: User)(implicit ec: ExecutionContext): Future[(WriteResult, User)] = {
        val user = item._id match {
            case Some(oid) => User(item._id, item.name, item.password, item.mobile, item.email)
            case None => User(Some(BSONObjectID.generate.stringify), item.name, item.password,
                item.mobile, item.email)
        }
        collection.update(Json.obj("_id" -> user._id), user, upsert = true).map(result => (result, user))
    }

    override def remove(selector: JsObject)(implicit ec: ExecutionContext): Future[WriteResult] =
        collection.remove(selector)

    override def edit(selector: JsObject, item: User)(implicit ec: ExecutionContext): Future[WriteResult] =
        collection.update(selector, item, upsert = false)

    override def update(selector: JsObject, update: JsObject)(implicit ec: ExecutionContext): Future[WriteResult] =
        collection.update(selector, update)

    override def findOne(selector: JsObject)(implicit ec: ExecutionContext): Future[Option[JsObject]] =
        collection.find(selector).one[JsObject]

    override def find(selector: JsObject)(implicit ec: ExecutionContext): Future[List[JsObject]] =
        collection.find(selector).cursor[JsObject](ReadPreference.primary).collect[List]()

}
