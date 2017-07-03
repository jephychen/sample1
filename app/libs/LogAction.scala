package libs

import play.api.Logger
import play.api.mvc._

import scala.concurrent.Future

/**
  * Created by chenshijue on 2017/7/3.
  */

object LogAction extends ActionBuilder[Request] {
    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
        Logger.info("Access to - " + request.method + " " + request.path)
        block(request)
    }
}

