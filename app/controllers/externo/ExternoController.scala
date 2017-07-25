package controllers.externo

import controllers.actions.Actions
import daos.MascotaDao
import format.DateFormatter
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc._
import javax.inject._
import scala.concurrent.Future

/** Controlador base de los usuario externos */
class ExternoController @Inject() (
    mascotaDao: MascotaDao,
    dateFormatter: DateFormatter,
    actions: Actions,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {

  implicit val dFmt = dateFormatter

  def index = actions.timedAction { implicit request ⇒
    Future.successful(Ok(views.html.externo.home(mascotaDao.mascotasCliente(request.session("login")))))
  }

}
