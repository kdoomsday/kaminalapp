package controllers

import controllers.actions.Actions
import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.Controller
import scala.concurrent.Future
import play.api.data.Forms._

/** Controlador para las acciones relacionadas con items */
class ItemController @Inject() (
    actions: Actions,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {

  import ItemController._

  def addItemView = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.items.addItem()))
  }

  def addClienteView = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.addCliente(clienteForm)))
  }

  def addCliente = TODO
}

object ItemController {
  val clienteForm: Form[String] = Form(
    single("nombre" → nonEmptyText)
  )
}
