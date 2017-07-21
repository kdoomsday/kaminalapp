package controllers.usuarios

import controllers.actions.Actions
import crypto.HashService
import daos.UserDao
import format.DateFormatter
import javax.inject.Inject
import models.Notification
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.Logger
import play.api.mvc.Controller
import scala.concurrent.Future

/** Controlador para las acciones de administracion de usuarios internos */
class UsuariosInternosController @Inject() (
    actions: Actions,
    userDao: UserDao,
    hasher: HashService,
    dateFormatter: DateFormatter,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  import UsuariosInternosController._

  implicit val dFmt = dateFormatter

  def usuariosInternos() = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.usuarios.usuariosInternos(userDao.usuariosInternos())))
  }

  def crearUsuarioInternoView() = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.usuarios.crearUsuario(formNuevoUsuario, "interno")))
  }

  def crearUsuarioInterno() = actions.roleAction("interno") { implicit req ⇒
    val salt = scala.util.Random.nextInt()

    val res = formNuevoUsuario.bindFromRequest.fold(
      formWithErrors ⇒ {
        Logger.debug("Error en datos de creacion de usuarios")
        implicit val nots = Notification.error(messagesApi("UsuariosInternosController.crear.error"))
        BadRequest(views.html.usuarios.crearUsuario(formWithErrors, "interno"))
      },
      usuario ⇒ {
        val (login, clave) = usuario
        val claveHashed = hasher.hashString(clave, salt)
        Logger.info(s"Creando usuario interno $login")
        userDao.crearUsuario(login, claveHashed, salt)

        implicit val nots = Notification.success(messagesApi("UsuariosInternosController.crear.success", login))
        Redirect(routes.UsuariosInternosController.usuariosInternos())
      }
    )

    Future.successful(res)
  }
}

object UsuariosInternosController {
  def formNuevoUsuario = Form(
    tuple(
      "login" → nonEmptyText,
      "clave" → nonEmptyText(minLength = 8)
    )
  )
}
