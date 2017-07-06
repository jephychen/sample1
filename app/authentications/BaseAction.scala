package authentications

import libs.{JwtExpireErrorException, JwtHelper, JwtValidateErrorException}
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by chenshijue on 2017/7/3.
  */

object BaseAction extends ActionBuilder[Request] {
    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
        logging(request)
        block(request)
    }

    def logging[A](request: Request[A]) = Logger.info("Access to - " + request.method + " " + request.path
        + request.headers.toSimpleMap.mkString(" | "))


    def authInvoke[A](request: Request[A], block: (Request[A] => Future[Result]), roleList: List[String]): Future[Result] = {
        val jwtOpt = request.session.get("jwt")
        if (!jwtOpt.isDefined) return Future(Unauthorized("Please login first"))

        JwtHelper.verifyAndDecode(jwtOpt.get) match {
            case Success(jwtBody) => {
                jwtBody.get("role") match {
                    case Some(role) => if (roleList.contains(role))
                        block(request) else Future(Unauthorized("Your role don't have the permission"))
                    case None => Future(Unauthorized("Get role info error"))
                }
            }
            case Failure(e) => {
                e match {
                    case JwtExpireErrorException() => Future(Unauthorized("Please login first").
                        withSession(request.session - "jwt"))
                    case JwtValidateErrorException() => Future(Unauthorized("Your session has been tampered"))
                }
            }
        }
    }}

