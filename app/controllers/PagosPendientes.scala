package controllers

import audits.EventDao
import controllers.actions.Actions
import daos.PagosDao
import javax.inject.Inject
import play.api.mvc.Controller
import play.api.Logger
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }

class PagosPendientes @Inject() (
    actions: Actions,
    eventDao: EventDao,
    pagosDao: PagosDao,
    val messagesApi: MessagesApi

) extends Controller with I18nSupport {

  def verPagosPendientes = actions.rAction("interno") { implicit request ⇒
    Ok(views.html.items.pagosPendientes(pagosDao.verPendientes()))
  }
}
