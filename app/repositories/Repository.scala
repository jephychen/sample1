package repositories

import play.api.libs.json.JsObject
import reactivemongo.api.commands.WriteResult

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by chenshijue on 2017/6/30.
  */
trait Repository[T] {

    def add(item: T)(implicit ec: ExecutionContext): Future[(WriteResult, T)]

    def remove(selector: JsObject)(implicit ec: ExecutionContext): Future[WriteResult]

    def removeAll()(implicit ec: ExecutionContext): Future[WriteResult]

    def update(item: T)(implicit ec: ExecutionContext): Future[WriteResult]

    def findOne(selector: JsObject)(implicit ec: ExecutionContext): Future[Option[JsObject]]

    def find(selector: JsObject)(implicit ec: ExecutionContext): Future[List[JsObject]]

}
