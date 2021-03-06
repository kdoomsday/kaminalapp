package controllers.externo

import audits.EventDao
import controllers.actions.Actions
import daos.{ MascotaDao, PagosDao }
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
    eventDao: EventDao,
    mascotaDao: MascotaDao,
    pagosDao: PagosDao,
    dateFormatter: DateFormatter,
    actions: Actions,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  import ExternoController._

  implicit val dFmt = dateFormatter

  def index = actions.rAction("usuario") { implicit request ⇒
    Ok(views.html.externo.home(mascotaDao.mascotasCliente(request.session("login"))))
  }

  def addPagoView = actions.rAction("usuario") { implicit request ⇒
    val mascotas = mascotaDao.mascotasCliente(request.session("login"))
    Ok(views.html.externo.pagoPendienteView(pagoPendienteForm, mascotas))
  }

  def addPago = actions.rAction("usuario") { implicit request ⇒
    pagoPendienteForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        val mascotas = mascotaDao.mascotasCliente(request.session("login"))
        BadRequest(views.html.externo.pagoPendienteView(formWithErrors, mascotas))
      },
      pagoPendiente ⇒ {
        pagosDao.addPendiente(pagoPendiente)
        eventDao.write(s"Agregado pago pendiente para mascota ${pagoPendiente.idMascota}")
        Redirect(controllers.routes.HomeController.index)
          .flashing("exito" → messagesApi("ExternoController.addPago.success"))
      }
    )
  }
}

object ExternoController {
  def pagoPendienteForm: Form[PagoPendiente] = Form(
    mapping(
      "id" → default(longNumber, 0L),
      "idMascota" → longNumber,
      "monto" → bigDecimal(16, 2).verifying("Monto debe ser positivo", m ⇒ m > 0),
      "confirmacion" → optional(nonEmptyText),
      "imagen" → optional(nonEmptyText)
    )(PagoPendiente.apply)(PagoPendiente.unapply)
  )
}
