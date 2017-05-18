package controllers

import org.joda.time.Instant
import org.scalatestplus.play._
import org.specs2._
import play.api.test._
import play.api.test.Helpers._
import testutil.TestUtil.injector

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with OneAppPerSuite {
  import testutil.TestDao

  val dao = injector.instanceOf[TestDao]
  def connectedUser() = dao.testUser.copy(lastActivity = Some(Instant.now()), connected = true)

  // After context para que se limpie un usuario al final de la prueba
  def cleanUser(login: String) = new mutable.After {
    def after = dao.eliminarUsuario(login)
  }

  "HomeController GET" should {

    "render the index page from a new instance of controller" in cleanUser(dao.testUser.login) {
      val controller = injector.instanceOf[HomeController]

      val user = dao.crearUsuario(connectedUser())

      val home = controller.index().apply(
        FakeRequest().withSession(
          "login" → user.login
        )
      )

      status(home) == OK &&
        contentType(home) == Some("text/html") &&
        contentAsString(home).contains("Welcome to Play")
    }

    "render the index page from the application" in cleanUser(dao.testUser.login) {
      val controller = app.injector.instanceOf[HomeController]
      val user = dao.crearUsuario(connectedUser())
      val home = controller.index().apply(
        FakeRequest().withSession("login" → user.login)
      )

      status(home) == OK &&
        contentType(home) == Some("text/html") &&
        contentAsString(home).contains("Welcome to Play")
    }

    "render the index page from the router" in cleanUser(dao.testUser.login) {
      val user = dao.crearUsuario(connectedUser())
      // Need to specify Host header to get through AllowedHostsFilter
      val request =
        FakeRequest(GET, "/").
          withHeaders("Host" → "localhost").
          withSession("login" → user.login)

      val home = route(app, request).get

      status(home) == OK &&
        contentType(home) == Some("text/html") &&
        contentAsString(home).contains("Welcome to Play")
    }
  }
}
