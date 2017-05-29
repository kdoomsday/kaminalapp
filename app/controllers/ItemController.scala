package controllers

import controllers.actions.Actions
import daos.ItemDao
import javax.inject.Inject
import models.Notification
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

  def addItemView = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.items.addItem()))
  }

  def addClienteView = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.addCliente(clienteForm)))
  }

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
}
