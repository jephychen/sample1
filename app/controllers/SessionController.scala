package controllers

import javax.inject._

import libs._
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.collection.JSONCollection
import repositories.UserRepository
import models.User
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chenshijue on 2017/7/4.
  */

@Singleton
class SessionController @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends Controller
    with MongoController with ReactiveMongoComponents {

    val userRepository = new UserRepository(reactiveMongoApi.db.collection[JSONCollection]("users"))

    def login = BaseAction.async{ implicit request =>
        val bodyJson = request.body.asJson.getOrElse(Json.obj())
        User.userReads.reads(bodyJson).fold(
            invalid => Future{ BadRequest("Parameter invalid. " + invalid.mkString(";"))},
            user => doLogin(user, request)
        )
    }

    def logout = BaseAction{ implicit request =>
        Ok("Successful logout").withSession(request.session - "jwt")
    }

    private def doLogin(user: User, request: Request[AnyContent]): Future[Result] = {
        user.mobile match {
            case Some(phone) => userRepository.findOne(Json.obj("mobile" -> phone)).map{
                case Some(userObj) => {
                    val _password = userObj.value.getOrElse("password", JsString("")).as[String]
                    val isValid = BCrypt.checkpw(user.password.getOrElse(""), _password)

                    if (isValid){
                        val _name = userObj.value.getOrElse("name", JsString("default name")).as[String]
                        val _mobile = userObj.value.getOrElse("mobile", JsString("")).as[String]
                        val _role = userObj.value.getOrElse("role", JsString("common")).as[String]
                        val jwt = JwtHelper.encode(Map("name" -> _name, "mobile" -> _mobile, "role" -> _role))
                        Ok("Successful login").withSession(request.session + ("jwt" -> jwt))
                    }else {
                        Forbidden("Password error").withSession(request.session - "jwt")
                    }
                }

                case None => Forbidden("User can't found").withSession(request.session - "jwt")
            }
            case None => Future{ BadRequest("Parameter invalid, no mobile found").
                withSession(request.session - "jwt")}
        }
    }
}
