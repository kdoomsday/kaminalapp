package controllers

import audits.EventDao
import controllers.actions.Actions
import daos.{ ClienteDao, MascotaDao }
import models.{ Mascota, Notification }
import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.{ Action, AnyContent, Controller }
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
      case Some(c) ⇒ Ok(views.html.mascota.datosMascota(mascotaForm, c, routes.MascotaController.addMascota(idCliente).toString))
      case None ⇒ {
        implicit val nots = Notification.warn(messagesApi("MascotaController.addMascotaView.clienteNoExiste"))
        Redirect(routes.HomeController.index())
      }
    }

    Future.successful(res)
  }

  /**
   * Registrar una mascota en el sistema para el cliente especificado.
   * Solo funciona si los datos del formulario son correctos y el cliente existe
   */
  def addMascota(idCliente: Long) = actions.roleAction("interno") { implicit req ⇒
    val response = clienteDao.byId(idCliente) match {
      case Some(c) ⇒ {
        mascotaForm.bindFromRequest.fold(
          formWithErrors ⇒ BadRequest(views.html.mascota.datosMascota(formWithErrors, c, routes.MascotaController.addMascota(idCliente).toString)),
          mascota ⇒ {
            mascotaDao.guardar(mascota)
            eventos.write(s"Mascota ${mascota.nombre} (${mascota.id}) registrada para ${c.nombreCompleto} ($idCliente)")
            implicit val nots = Notification.success(messagesApi("MascotaController.addMascota.exito", mascota.nombre))
            Redirect(routes.ClienteController.cliente(idCliente))
          }
        )
      }
      case None ⇒ {
        implicit val nots = Notification.warn(messagesApi("MascotaController.addMascotaView.clienteNoExiste"))
        Redirect(routes.HomeController.index())
      }
    }

    Future.successful(response)
  }

  def editMascotaView(idMascota: Long) = actions.rAction("interno") { implicit req ⇒
    mascotaDao.byId(idMascota) match {
      case Some(mascota) ⇒
        Ok(views.html.mascota.datosMascota(
          mascotaForm.fill(mascota),
          clienteDao.byId(mascota.idCliente).get, // Asumo que hay cliente
          routes.MascotaController.editMascota(idMascota).toString,
          Some(idMascota)
        ))

      case None ⇒ {
        val msg = messagesApi("MascotaController.editMascotaView.noMascota")
        implicit val nots = Notification.warn(msg)
        Redirect(routes.HomeController.index())
          .flashing("warn" → msg)
      }
    }
  }

  def editMascota(idMascota: Long): Action[AnyContent] = actions.rAction("interno") { implicit req ⇒
    mascotaDao.byIdConCliente(idMascota) match {
      case Some((m, cliente)) ⇒ {
        mascotaForm.bindFromRequest.fold(
          formWithErrors ⇒ BadRequest(views.html.mascota.datosMascota(
            formWithErrors,
            cliente,
            routes.MascotaController.editMascota(idMascota).toString(),
            Some(idMascota)
          )),
          mascota ⇒ {
            mascotaDao.editar(mascota)
            eventos.write(s"Mascota ${mascota.nombre} (${mascota.id}) editada")
            implicit val nots = Notification.success(messagesApi("MascotaController.editMascota.exito"))
            Redirect(routes.ClienteController.cliente(mascota.idCliente))
          }
        )
      }
      case None ⇒ {
        Redirect(routes.HomeController.index)
          .flashing("warn" → messagesApi("MascotaController.editMascota.noExiste"))
      }
    }
  }
}

object MascotaController {
  def mascotaForm = Form(
    mapping(
      "id" → default(longNumber, 0L),
      "nombre" → nonEmptyText,
      "raza" → optional(nonEmptyText),
      "edad" → optional(number(min = 0)),
      "fechaInicio" → optional(jodaDate(pattern = "yyyy-MM-dd")),
      "idCliente" → longNumber
    )(Mascota.apply)(Mascota.unapply)
  )
}
