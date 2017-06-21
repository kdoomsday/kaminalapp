package controllers

import audits.EventDao
import controllers.actions.Actions
import daos.{ ClienteDao, MascotaDao }
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
    mascotaDao: MascotaDao,
    eventos: EventDao,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  import MascotaController._

  /** Ir a la vista de registro de mascotas. El cliente tiene que existir */
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


  /** Registrar una mascota en el sistema para el cliente especificado.
    * Solo funciona si los datos del formulario son correctos y el cliente existe
    */
  def addMascota(idCliente: Long) = actions.roleAction("interno") { implicit req =>
    val response = clienteDao.byId(idCliente) match {
      case Some(c) => {
        mascotaForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.mascota.addMascota(formWithErrors, c)),
          mascota => {
            mascotaDao.guardar(mascota, idCliente)
            eventos.write(s"Mascota ${mascota.nombre} (${mascota.id}) registrada para ${c.nombreCompleto} ($idCliente)")
            // regresar al detalle del cliente
            Redirect(routes.ClienteController.cliente(idCliente))
          }
        )
      }
      case None => {
        implicit val nots = Notification.warn(messagesApi("MascotaController.addMascotaView.clienteNoExiste"))
        Redirect(routes.HomeController.index())
      }
    }

    Future.successful(response)
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
