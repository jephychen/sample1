package routers

import javax.inject.Inject

import controllers.UserController
import play.api.routing.Router.Routes
import play.api.routing.sird._
import play.api.routing.SimpleRouter

/**
  * Created by chenshijue on 2017/6/28.
  */
class UserRouter @Inject() (controller: UserController) extends SimpleRouter {
    override def routes: Routes = {
        case GET(p"/") =>
            controller.getUsers
        case GET(p"/$id") =>
            controller.getUser(id: String)
        case POST(p"/") =>
            controller.createUser
        case PUT(p"/") =>
            controller.editUser
        case PATCH(p"/") =>
            controller.updateUser
        case DELETE(p"/$id") =>
            controller.removeUser(id: String)
    }
}
