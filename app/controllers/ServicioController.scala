package controllers

import audits.EventDao
import controllers.actions.Actions
import daos.ServicioDao
import javax.inject.Inject
import models.{ Notification, Servicio }
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import scala.concurrent.Future

/** Controlador para las acciones relacionadas con servicios ofrecidos */
class ServicioController @Inject() (
    actions: Actions,
    servicioDao: ServicioDao,
    eventDao: EventDao,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  import ServicioController._

  def addServicioView = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.servicio.dataServicio(servicioForm, routes.ServicioController.addServicio.toString)))
  }

  def addServicio = actions.roleAction("interno") { implicit req ⇒
    val res = servicioForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        for (e ← formWithErrors.errors) println(e)
        BadRequest(views.html.servicio.dataServicio(formWithErrors, routes.ServicioController.addServicio.toString))
      },
      servicio ⇒ {
        servicioDao.registrar(servicio.nombre, servicio.precio, servicio.mensual)
        eventDao.write(s"Creado servicio ${servicio.nombre} precio = ${servicio.precio} mensual = ${servicio.mensual}")
        implicit val nots = Notification.success(messagesApi("ServicioController.add.success", servicio.nombre))
        Redirect(routes.ServicioController.servicios)
      }
    )

    Future.successful(res)
  }

  def editServicioView(idServicio: Long) = actions.rAction("interno") { implicit req ⇒
    servicioDao.byId(idServicio) match {
      case Some(servicio) ⇒ {
        val form = servicioForm.fill(servicio)
        Ok(views.html.servicio.dataServicio(form, routes.ServicioController.editServicio.toString))
      }
      case None ⇒
        Redirect(routes.ServicioController.servicios)
          .flashing("warn" → messagesApi("ServicioController.edit.noExiste", idServicio))
    }
  }

  def editServicio = actions.rAction("interno") { implicit req ⇒
    servicioForm.bindFromRequest.fold(
      formWithErrors ⇒ BadRequest(views.html.servicio.dataServicio(formWithErrors, routes.ServicioController.editServicio.toString)),
      servicio ⇒ {
        servicioDao.actualizar(servicio)
        eventDao.write(messagesApi("ServicioController.edit.exito", servicio.id))
        Redirect(routes.ServicioController.servicios)
      }
    )
  }

  def servicios = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.servicio.servicios(
      servicioDao.todos
    )))
  }
}

object ServicioController {
  val servicioForm: Form[Servicio] = Form(
    mapping(
      "id" → default(longNumber, 0L),
      "nombre" → nonEmptyText,
      "precio" → bigDecimal(16, 2),
      "mensual" → boolean
    )(Servicio.apply)(Servicio.unapply)
  )
}
