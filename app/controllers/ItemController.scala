package controllers

import controllers.actions.Actions
import javax.inject.Inject
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.Controller
import scala.concurrent.Future

/** Controlador para las acciones relacionadas con items */
class ItemController @Inject() (
    actions: Actions,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {

  def addItemView = actions.timedAction { implicit req ⇒
    Future.successful(Ok(views.html.items.addItem()))
  }
}
