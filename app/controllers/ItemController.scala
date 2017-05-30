package controllers

import controllers.actions.Actions
import daos.ItemDao
import javax.inject.Inject
import models.Notification
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc.Controller
import scala.concurrent.Future
import play.api.data.Forms._

/** Controlador para las acciones relacionadas con items */
class ItemController @Inject() (
    actions: Actions,
    itemDao: ItemDao,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {

  import ItemController._

  // Vista de agregar un item
  def addItemView = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.items.addItem(itemForm, itemDao.clientes())))
  }

  // Accion de agregar un item
  def addItem = actions.roleAction("interno") { implicit req ⇒
    val res = itemForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        Logger.debug("Error de datos agregando item")
        BadRequest(views.html.items.addItem(formWithErrors, itemDao.clientes()))
      },

      item ⇒ {
        val (id, monto) = item
        itemDao.add(id, monto)
        Logger.debug(s"Agregado item para cliente $id con  monto $monto")
        implicit val nots = Notification.success(Messages("ItemController.addItem.success"))
        Ok(views.html.items.addItem(itemForm, itemDao.clientes()))
      }
    )

    Future.successful(res)
  }

  // Vista de agregar un cliente
  def addClienteView = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.addCliente(clienteForm)))
  }

  // Vista del listado de los clientes
  def clientes = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.clientes(itemDao.clientes())))
  }

  // Vista de agregar un cliente
  def addCliente = actions.roleAction("interno") { implicit req ⇒
    val result = clienteForm.bindFromRequest.fold(
      formWithErrors ⇒ BadRequest(views.html.addCliente(formWithErrors)),
      nombre ⇒ {
        itemDao.addCliente(nombre)
        implicit val notifications = Notification.success(Messages("ItemController.addCliente.success", nombre))
        Ok(views.html.addCliente(clienteForm))
      }
    )

    Future.successful(result)
  }
}

object ItemController {
  val clienteForm: Form[String] = Form(
    single("nombre" → nonEmptyText)
  )

  val itemForm: Form[(Long, BigDecimal)] = Form(
    tuple(
      "cliente" → longNumber,
      "monto" → bigDecimal(16, 2)
    )
  )
}
