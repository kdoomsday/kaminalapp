package controllers

import audits.EventDao
import controllers.actions.Actions
import daos.PagosDao
import javax.inject.Inject
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import play.api.Logger
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import json.JsonHelpers._

class PagosPendientes @Inject() (
    actions: Actions,
    eventDao: EventDao,
    pagosDao: PagosDao,
    val messagesApi: MessagesApi

) extends Controller with I18nSupport {
  import PagosPendientes._

  def verPagosPendientes = actions.rAction("interno") { implicit request ⇒
    Ok(views.html.items.pagosPendientes(pagosDao.verPendientes()))
  }

  def confirmarPago = actions.rAction("interno") { implicit request ⇒
    pagoForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        Ok(jsonErr(messagesApi("PagosPendientes.confirmarPago.err")))
      },
      id ⇒ {
        val item = pagosDao.confirmarPago(id)
        eventDao.write(s"Pago confirmado") // TODO
        Ok("ok")
      }
    )
  }
}

object PagosPendientes {
  val pagoForm: Form[Long] = Form(
    single(
      "id" → longNumber
    )
  )
}
