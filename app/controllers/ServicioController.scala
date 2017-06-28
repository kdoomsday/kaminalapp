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
    Future.successful(Ok(views.html.servicio.addServicio(servicioForm)))
  }

  def addServicio = actions.roleAction("interno") { implicit req ⇒
    val res = servicioForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        for (e ← formWithErrors.errors) println(e)
        BadRequest(views.html.servicio.addServicio(formWithErrors))
      },
      servicio ⇒ {
        servicioDao.registrar(servicio.nombre, servicio.precio, servicio.mensual)
        eventDao.write(s"Creado servicio ${servicio.nombre} precio = ${servicio.precio} mensual = ${servicio.mensual}")
        implicit val nots = Notification.success(messagesApi("ServicioController.add.success", servicio.nombre))
        Ok(views.html.index()) // TODO redirigir a vista de servicios
      }
    )

    Future.successful(res)
  }
}

object ServicioController {
  val servicioForm: Form[Servicio] = Form(
    mapping(
      "id" → ignored(0L),
      "nombre" → nonEmptyText,
      "precio" → bigDecimal(16, 2),
      "mensual" → boolean
    )(Servicio.apply)(Servicio.unapply)
  )
}
