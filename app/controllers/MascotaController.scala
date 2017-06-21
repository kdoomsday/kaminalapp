package controllers

import controllers.actions.Actions
import daos.ClienteDao
import models.{ Mascota, Notification }
import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.Controller
import scala.concurrent.Future

/** Controlador para las acciones relacionadas con mascotas */
class MascotaController @Inject() (
    actions: Actions,
    clienteDao: ClienteDao,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  import MascotaController._

  def addMascotaView(idCliente: Long) = actions.roleAction("interno") { implicit req ⇒
    val res = clienteDao.byId(idCliente) match {
      case Some(c) ⇒ Ok(views.html.mascota.addMascota(mascotaForm, c))
      case None ⇒ {
        implicit val nots = Notification.warn(messagesApi("MascotaController.addMascotaView.clienteNoExiste"))
        Redirect(routes.HomeController.index())
      }
    }

    Future.successful(res)
  }


  def addMascota() = actions.roleAction("interno") { implicit req =>
    mascotaForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.mascota.addMascota(formWithErrors, c))
    )
  }
}

object MascotaController {
  def mascotaForm = Form(
    mapping(
      "id" → ignored(0L),
      "nombre" → nonEmptyText,
      "raza" → optional(nonEmptyText),
      "edad" → optional(number(min = 0)),
      "fechaInicio" → optional(jodaDate(pattern = "yyyy-MM-dd"))
    )(Mascota.apply)(Mascota.unapply)
  )
}
