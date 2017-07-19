package controllers.usuarios

import audits.EventDao
import controllers.actions.Actions
import crypto.HashService
import daos.UserDao
import javax.inject.Inject
import json.JsonHelpers
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.Controller

/** Controlador de las acciones de usuarios de cualquier tipo */
class UsuarioController @Inject() (
    userDao: UserDao,
    hashService: HashService,
    actions: Actions,
    eventDao: EventDao,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  import UsuarioController._

  def cambiarClaveView = actions.userAction { implicit req ⇒
    userDao.byLogin(req.session("login")) match {
      case Some(u) ⇒
        Ok(views.html.usuarios.cambioClave(u.id))
      case None ⇒
        Redirect(_root_.controllers.routes.HomeController.index)
    }
  }

  def cambiarClave = actions.userAction { implicit req ⇒
    Logger.debug(s"Llamado a cambiar clave")
    cambioClaveForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(
          JsonHelpers.jsonErr(
            messagesApi("UsuarioController.cambioClave.bindError")
          )
        )
      },

      data ⇒ {
        val (id, clave) = data
        val salt = scala.util.Random.nextInt()
        userDao.actualizarClave(id, hashService.hashString(clave, salt), salt)
        eventDao.write(s"Clave actualizada para el usuario $id")
        Ok(JsonHelpers.jsonOk())
      }
    )
  }

}

object UsuarioController {
  val cambioClaveForm = Form(
    tuple(
      "id" → longNumber,
      "nuevaClave" → nonEmptyText(minLength = 8)
    )
  )
}
