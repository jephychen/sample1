package libs

import play.api.Logger
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.Future
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chenshijue on 2017/7/3.
  */

object BaseAction extends ActionBuilder[Request] {
    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
        logging(request)
        block(request)
    }

    def logging[A](request: Request[A]) = Logger.info("Access to - " + request.method + " " + request.path)


    def authInvoke[A](request: Request[A], block: (Request[A] => Future[Result]), roleList: List[String]): Future[Result] = {
        val jwtOpt = request.session.get("jwt")
        if (!jwtOpt.isDefined) return Future(Unauthorized("You don't have the permission"))

        JwtHelper.verifyAndDecode(jwtOpt.get) match {
            case Success(jwtBody) => {
                jwtBody.get("role") match {
                    case Some(role) => if (roleList.contains(role))
                        block(request) else Future(Unauthorized("You don't have the permission"))
                    case None => Future(Unauthorized("You don't have the permission"))
                }
            }
            case Failure(e) => Future(Unauthorized("You don't have the permission"))
        }
    }}

