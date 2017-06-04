package controllers

import audits.EventDao
import controllers.actions.Actions
import daos.{ ClienteDao, ItemDao }
import javax.inject.Inject
import models.{ Item, Notification }
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc.Controller
import scala.concurrent.Future

/** Controlador para acciones que tienen que ver con clientes */
class ClienteController @Inject() (
    actions: Actions,
    clienteDao: ClienteDao,
    itemDao: ItemDao,
    eventDao: EventDao,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  import ClienteController.clienteForm

  // Vista del listado de los clientes
  def clientes = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.cliente.clientes(clienteDao.clientesSaldo())))
  }

  // Accion de agregar el cliente nuevo
  def addCliente = actions.roleAction("interno") { implicit req ⇒
    val result = clienteForm.bindFromRequest.fold(
      formWithErrors ⇒ BadRequest(views.html.cliente.addCliente(formWithErrors)),
      nombre ⇒ {
        clienteDao.addCliente(nombre)
        eventDao.write(s"Cliente $nombre agregado")
        implicit val notifications = Notification.success(Messages("ClienteController.addCliente.success", nombre))
        Ok(views.html.cliente.addCliente(clienteForm))
      }
    )

    Future.successful(result)
  }

  // Vista de agregar un cliente
  def addClienteView = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.cliente.addCliente(clienteForm)))
  }

  // Detalles de un cliente segun su id, como deben ser vistos por un usuario interno
  def cliente(id: Long) = actions.roleAction("interno") { implicit req ⇒
    val oDatos = itemDao.datosCliente(id)
    Future.successful(
      oDatos.fold {
        implicit val nots = Notification.warn(Messages("ClienteController.cliente.noExiste"))
        (Ok(views.html.index()))
      } { datos: (String, List[Item]) ⇒
        val (nombre, items) = datos
        val saldo = items.foldLeft(BigDecimal(0))((acc, i) ⇒ acc + i.monto)
        Ok(views.html.cliente.cliente(nombre, saldo, items))
      }
    )
  }
}

object ClienteController {
  val clienteForm: Form[String] = Form(
    single("nombre" → nonEmptyText)
  )
}
