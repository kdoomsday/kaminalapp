package controllers.actions

import play.api.mvc.{ Action, AnyContent, BodyParser, Controller, Result }

import daos.UserDao
import models.User

import org.joda.time.Instant
import com.github.nscala_time.time.Imports._

import scala.concurrent.Future

import be.objectify.deadbolt.scala.{ DeadboltActions, AuthenticatedRequest }

import javax.inject.Inject

class Actions @Inject() (
    deadbolt: DeadboltActions,
    userDao: UserDao
) extends Controller {
  val timeout = 10.minutes

  /** Add session timeout checking to an action */
  def timedAction[A](parser: BodyParser[A] = parse.default)(block: AuthenticatedRequest[A] ⇒ Future[Result]): Action[A] = {
    deadbolt.SubjectPresent()(parser) { authRequest ⇒
      userChecks(authRequest, validators, block(authRequest))
    }
  }

  def timedAction(block: AuthenticatedRequest[_] ⇒ Future[Result]): Action[AnyContent] =
    timedAction(parse.default)(block)

  /** Action that checks for a role and session timeout */
  def roleActionP[A](rolename: String)(parser: BodyParser[A] = parse.default)(block: AuthenticatedRequest[A] ⇒ Future[Result]): Action[A] =
    {
      deadbolt.Restrict(List(Array(rolename)))(parser) { authRequest ⇒
        userChecks(authRequest, validators, block(authRequest))
      }
    }

  def roleAction(rolename: String)(block: AuthenticatedRequest[_] ⇒ Future[Result]): Action[AnyContent] =
    roleActionP(rolename)(parse.default)(block)

  /** roleAction from an action that directly returns a result, instead of a Future */
  def rAction(rolename: String)(block: AuthenticatedRequest[_] ⇒ Result): Action[AnyContent] =
    roleAction(rolename)(authRequest ⇒ Future.successful(block(authRequest)))

  /**
   * Acción que requiere un usuario logueado y más nada No hay revisión de
   * tiempo de sesión ni ninguna otra cosa.
   */
  def userActionF[A](
    parser: BodyParser[A] = parse.default
  ): ((AuthenticatedRequest[A]) ⇒ Future[Result]) ⇒ Action[A] =
    deadbolt.SubjectPresent()(parser)_

  /** Igual que userActionF pero puede devolver Result en vez de Future[Result] */
  def userAction[A](block: AuthenticatedRequest[_] ⇒ Result) = userActionF() { req ⇒
    Future.successful(block(req))
  }

  /** Applies a session timeout check. Is used for combining into other actions */
  private[this] def timeCheck[A](req: AuthenticatedRequest[A], block: AuthenticatedRequest[A] ⇒ Future[Result]): Future[Result] =
    userDao.byLogin(req.session.get("login").get) match {
      case Some(u) ⇒
        if (isStillIn(u)) {
          userDao.updateConnected(u.login)
          block(req)
        } else Future.successful(notLoggedIn)
      case None ⇒
        Future.successful(notLoggedIn)
    }

  /** Whether a user is connected and not timed out */
  def isStillIn(u: User): Boolean =
    u.connected &&
      u.lastActivity.fold(false)(_ + timeout >= Instant.now())

  /** What to do if not logged in */
  lazy val notLoggedIn: Result =
    Redirect(controllers.routes.LoginController.loginPage)

  /** Qué hacer si requiere cambio de contraseña */
  private[this] lazy val pwdChange: Result = Redirect(controllers.usuarios.routes.UsuarioController.cambiarClaveView)

  /**
   * Aplicar una lista de validaciones a un usuario. Cada validacion produce
   * un resultado en caso de fallar. El resultado es el que produzca la primera
   * validacion que falle o, si todas pasan, ifok
   */
  private[this] def checkList(
    u: User,
    validations: List[User ⇒ Option[Result]],
    ifOk: ⇒ Future[Result]
  ): Future[Result] =
    {
      def cl(vals: List[User ⇒ Option[Result]]): Future[Result] = vals match {
        case Nil ⇒ ifOk
        case v :: vs ⇒ {
          v(u) match {
            case Some(result) ⇒ Future.successful(result)
            case None         ⇒ cl(vs)
          }
        }
      }

      cl(validations)
    }

  /**
   * Buscar el usuario en la sesion y, si está, aplicar checkList con los
   * los validadores
   */
  private[this] def userChecks(
    req: AuthenticatedRequest[_],
    validators: List[User ⇒ Option[Result]],
    ifOk: ⇒ Future[Result]
  ): Future[Result] =
    {
      userDao.byLogin(req.session.get("login").get) match {
        case Some(u) ⇒
          checkList(u, validators, ifOk)
        case None ⇒
          Future.successful(notLoggedIn)
      }
    }

  /** Validacion de tiempo de sesion */
  private[this] def timeVal(u: User): Option[Result] = {
    if (isStillIn(u)) {
      userDao.updateConnected(u.login)
      None
    } else {
      Some(notLoggedIn)
    }
  }

  /** Validación de cambio de contraseña */
  private[this] def pwdChangeVal(u: User): Option[Result] =
    if (u.cambioClave) {
      userDao.updateConnected(u.login)
      Some(pwdChange)
    } else None

  /** Chequeos que se hacen al usuario en cada acción */
  private[this] lazy val validators: List[User ⇒ Option[Result]] =
    List(timeVal, pwdChangeVal)
}
