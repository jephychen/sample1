package authentications

import models.UserRole
import play.api.mvc._

import scala.concurrent.Future
/**
  * Created by chenshijue on 2017/7/5.
  */
object AuthAdminAction extends ActionBuilder[Request]{

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
        BaseAction.logging(request)
        BaseAction.authInvoke(request, block, UserRole.authAdminList)
    }

}
