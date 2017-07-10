package controllers

import audits.EventDao
import controllers.actions.Actions
import daos.{ ClienteDao, ItemDao, ServicioDao }
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
    servicioDao: ServicioDao,
    eventDao: EventDao,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {

  import ItemController._

  // Vista de agregar un item
  def addItemView(idMascota: Long) = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.items.addItem(itemForm, itemServicioForm, idMascota, servicioDao.todos)))
  }

  // Accion de agregar un item
  def addItem(idMascota: Long) = actions.roleAction("interno") { implicit req ⇒
    val res = itemForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        Logger.debug("Error de datos agregando item")
        BadRequest(views.html.items.addItem(formWithErrors, itemServicioForm, idMascota, servicioDao.todos))
      },

      item ⇒ {
        val (idMascota, monto, descripcion) = item
        itemDao.add(idMascota, monto, descripcion)
        val cliente = clienteDao.byMascota(idMascota).get // Si todo funciono, el cliente tiene que existir
        eventDao.write(s"Agregado item para cliente ${cliente.nombreCompleto} (${cliente.id}), mascota $idMascota con  monto $monto")
        // implicit val nots = Notification.success(Messages("ItemController.addItem.success"))
        Redirect(routes.ClienteController.cliente(cliente.id)).flashing("success" → Messages("ItemController.addItem.success"))
      }
    )

    Future.successful(res)
  }

  def addItemServicio(idMascota: Long) = actions.rAction("interno") { implicit req ⇒
    itemServicioForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        for (e ← formWithErrors.errors)
          Logger.debug(e.toString())
        BadRequest(views.html.items.addItem(itemForm, formWithErrors, idMascota, servicioDao.todos))
      },
      idServicio ⇒ {
        itemDao.addByServicio(idMascota, idServicio)
        eventDao.write(s"Agregado servicio $idServicio a la mascota $idMascota")

        clienteDao.byMascota(idMascota) match {
          case Some(c) ⇒
            Redirect(routes.ClienteController.cliente(c.id))
              .flashing("exito" → messagesApi("ItemController.addItem.success"))

          case None ⇒
            Redirect(routes.ClienteController.clientes())
              .flashing("warn" → messagesApi("ItemController.addItem.noCliente"))
        }
      }
    )
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
      "mascota" → longNumber,
      "monto" → bigDecimal(16, 2),
      "descripcion" → text
    )
  )

  val itemServicioForm: Form[Long] = Form(
    single(
      "servicio" → longNumber
    )
  )

  val eliminarForm: Form[Long] = Form(
    single("id" → longNumber)
  )
}
