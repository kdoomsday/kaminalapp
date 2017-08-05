package controllers.externo

import controllers.actions.Actions
import daos.MascotaDao
import format.DateFormatter
import models.PagoPendiente
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import javax.inject._
import scala.concurrent.Future

/** Controlador base de los usuario externos */
class ExternoController @Inject() (
    mascotaDao: MascotaDao,
    dateFormatter: DateFormatter,
    actions: Actions,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  import ExternoController._

  implicit val dFmt = dateFormatter

  def index = actions.rAction("usuario") { implicit request ⇒
    Ok(views.html.externo.home(mascotaDao.mascotasCliente(request.session("login"))))
  }

  def agregarPagoView = actions.rAction("usuario") { implicit request ⇒
    val mascotas = mascotaDao.mascotasCliente(request.session("login"))
    Ok(views.html.externo.pagoPendienteView(pagoPendienteForm, mascotas))
  }

}

object ExternoController {
  def pagoPendienteForm: Form[PagoPendiente] = Form(
    mapping(
      "id" → default(longNumber, 0L),
      "idMascota" → longNumber,
      "monto" → bigDecimal(16, 2),
      "confirmacion" → optional(nonEmptyText),
      "imagen" → optional(nonEmptyText)
    )(PagoPendiente.apply)(PagoPendiente.unapply)
  )
}
