package controllers.usuarios

import controllers.actions.Actions
import daos.UserDao
import javax.inject.Inject
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.Controller
import scala.concurrent.Future

/** Controlador para las acciones de administracion de usuarios internos */
class UsuariosInternosController @Inject() (
    actions: Actions,
    userDao: UserDao,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {

  def usuariosInternos() = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.usuarios.usuariosInternos(userDao.usuariosInternos())))
  }
}
