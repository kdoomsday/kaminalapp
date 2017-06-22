package controllers

import audits.EventDao
import controllers.actions.Actions
import daos.{ ClienteDao, ItemDao }
import format.DateFormatter
import javax.inject.Inject
import models.{ Cliente, Item, Mascota, Notification }
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
    dateFormatter: DateFormatter,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  import ClienteController.clienteForm

  implicit val dFmt = dateFormatter

  // Vista del listado de los clientes
  def clientes = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.cliente.clientes(clienteDao.clientesSaldo())))
  }

  // Accion de agregar el cliente nuevo
  def addCliente = actions.roleAction("interno") { implicit req ⇒
    val result = clienteForm.bindFromRequest.fold(
      formWithErrors ⇒ BadRequest(views.html.cliente.addCliente(formWithErrors)),
      cliente ⇒ {
        clienteDao.addCliente(cliente)
        eventDao.write(s"Cliente ${cliente.nombre} ${cliente.apellido} agregado")
        implicit val notifications = Notification.success(Messages("ClienteController.addCliente.success", s"${cliente.nombre} ${cliente.apellido}"))
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
      } { datos: (Cliente, List[Mascota], List[Item]) ⇒
        val (cliente, mascotas, items) = datos
        val saldo = items.foldLeft(BigDecimal(0))((acc, i) ⇒ acc + i.monto)
        Ok(views.html.cliente.cliente(cliente, mascotas, saldo, items))
      }
    )
  }
}

object ClienteController {
  // val clienteForm: Form[String] = Form(
  //   single("nombre" → nonEmptyText)
  // )
  val clienteForm: Form[Cliente] = Form(
    mapping(
      "id" → ignored(0L),
      "nombre" → nonEmptyText,
      "apellido" → nonEmptyText,
      "direccion" → optional(text),
      "email" → optional(email)
    )(Cliente.apply)(Cliente.unapply)
  )
}
