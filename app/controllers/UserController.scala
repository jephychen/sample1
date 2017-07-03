package controllers

import javax.inject._

import libs.LogAction
import play.api.mvc._
import models.User
import play.api.libs.json.Json
import play.modules.reactivemongo._
import play.modules.reactivemongo.json._
import reactivemongo.play.json.collection.JSONCollection
import repositories.UserRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chenshijue on 2017/6/28.
  */

@Singleton
class UserController @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends Controller
    with MongoController with ReactiveMongoComponents{

    val userRepository = new UserRepository(reactiveMongoApi.db.collection[JSONCollection]("users"))

    def getUsers = LogAction.async {
        userRepository.find(Json.obj()).map(users => {
                Ok(Json.toJson(for (user <- users) yield user - "password"))
            })
    }

    def getUser(id: String) = LogAction.async {
        userRepository.findOne(Json.obj("_id" -> id)).map{
                case Some(user) => Ok(user - "password")
                case None => Ok("No result for " + id)
            }
    }

    def createUser = LogAction.async { implicit request =>
        val bodyJson = request.body.asJson.getOrElse(Json.obj())
        User.userReads.reads(bodyJson).fold(
            invalid => Future{ BadRequest("Parameter invalid. " + invalid.mkString(";"))},
            user => {
                userRepository.add(user).map(
                    result => if (result._1.inError) InternalServerError(result._1.errmsg.getOrElse(""))
                        else Ok(User.userOWrites.writes(result._2) - "password")
                )
            }
        )
    }

    def updateUser = LogAction.async { implicit request =>
        val bodyJson = request.body.asJson.getOrElse(Json.obj())
        User.userReads.reads(bodyJson).fold(
            invalid => Future{ BadRequest("Parameter invalid. " + invalid.mkString(";"))},
            user => {
                userRepository.update(user).map(
                    result => if (result.inError) InternalServerError(result.errmsg.getOrElse(""))
                    else Ok(Json.toJson("update success"))
                )
            }
        )
    }

    def removeAllUser = LogAction.async {
        userRepository.removeAll().map(
            result => if (result.inError) InternalServerError(result.errmsg.getOrElse(""))
            else Ok(Json.toJson("remove all success"))
        )
    }

    def removeUser(id: String) = LogAction.async {
        userRepository.remove(Json.obj("_id" -> id)).map(
            result => if (result.inError) InternalServerError(result.errmsg.getOrElse(""))
            else Ok(Json.toJson("remove success"))
        )
    }

}
