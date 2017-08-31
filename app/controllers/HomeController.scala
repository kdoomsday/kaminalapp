package controllers

import controllers.actions.Actions
import daos.UserDao
import format.DateFormatter
import javax.inject._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc._
import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
    userDao: UserDao,
    dateFormatter: DateFormatter,
    val actions: Actions,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {

  implicit val dFmt = dateFormatter

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = actions.timedAction { implicit request ⇒
    val res = userDao.byLoginWithRole(request.session("login")) match {
      case Some((user, rol)) ⇒ if (rol.name == "interno") Ok(views.html.index())
      else Redirect(controllers.externo.routes.ExternoController.index)

      case None ⇒ actions.notLoggedIn
    }

    Future.successful(res)
  }

}
