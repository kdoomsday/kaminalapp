import play.api.http.DefaultHttpErrorHandler
import daos.SubjectDao
import be.objectify.deadbolt.scala.AuthenticatedRequest
import play.api.OptionalSourceMapper
import play.api.Environment
import javax.inject.Inject
import models.Notification
import play.api.Configuration
import play.api.Logger
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router
import scala.concurrent._
import javax.inject._

@Singleton
class ErrorHandler @Inject() (
  subjectDao: SubjectDao,
  env: Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: Provider[Router],
  val messagesApi: MessagesApi
)
    extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with I18nSupport {

  def onProdServerError(request: RequestHeader, exception: Throwable) = {
    Logger.error("Error no manejado", exception)
    val req: Request[Any] = Request(request, 1)
    val subject = subjectDao.subjectByIdentifier(request.session("login"))

    implicit val authReq: AuthenticatedRequest[_] = AuthenticatedRequest(req, subject)
    implicit val nots = Notification.error(messagesApi("ErrorHandler.error"))
    Future.successful(
      Redirect(controllers.routes.HomeController.index)
    )
  }
}
