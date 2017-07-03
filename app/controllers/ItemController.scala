package controllers

import audits.EventDao
import controllers.actions.Actions
import daos.{ ItemDao, ClienteDao }
import javax.inject.Inject
import models.Notification
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc.Controller
import scala.concurrent.Future

/** Controlador para las acciones relacionadas con items */
class ItemController @Inject() (
    actions: Actions,
    itemDao: ItemDao,
    clienteDao: ClienteDao,
    eventDao: EventDao,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {

  import ItemController._

  // Vista de agregar un item
  def addItemView = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.items.addItem(itemForm, clienteDao.clientes())))
  }

  // Accion de agregar un item
  def addItem = actions.roleAction("interno") { implicit req ⇒
    val res = itemForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        Logger.debug("Error de datos agregando item")
        BadRequest(views.html.items.addItem(formWithErrors, clienteDao.clientes()))
      },

      item ⇒ {
        val (id, monto, descripcion) = item
        itemDao.add(id, monto, descripcion)
        eventDao.write(s"Agregado item para cliente $id con  monto $monto")
        implicit val nots = Notification.success(Messages("ItemController.addItem.success"))
        Ok(views.html.items.addItem(itemForm, clienteDao.clientes()))
      }
    )

    Future.successful(res)
  }

  /**
   * Elimina un item por id, recibido como form.
   * @return Ok("ok") si lo elimin&oacute;. BadRequest si no
   */
  def eliminar() = actions.roleAction("interno") { implicit req ⇒
    val res = eliminarForm.bindFromRequest.fold(
      formWithErrors ⇒ BadRequest("error"),
      idItem ⇒ {
        if (itemDao.eliminar(idItem)) {
          eventDao.write(s"Eliminado item $idItem")
          Ok("ok")
        } else {
          BadRequest("error")
        }
      }
    )
    Future.successful(res)
  }
}

object ItemController {

  val itemForm: Form[(Long, BigDecimal, String)] = Form(
    tuple(
      "cliente" → longNumber,
      "monto" → bigDecimal(16, 2),
      "descripcion" → text
    )
  )

  val eliminarForm: Form[Long] = Form(
    single("id" → longNumber)
  )
}
