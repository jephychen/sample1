package routers

import javax.inject.Inject

import controllers.SessionController
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Created by chenshijue on 2017/7/4.
  */
class SessionRouter @Inject() (controller: SessionController) extends SimpleRouter {
    override def routes: Routes = {
        case POST(p"/") => controller.login
        case DELETE(p"/") => controller.logout
    }
}
