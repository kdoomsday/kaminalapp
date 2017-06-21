package bindings

import audits.{ EventDao, EventDaoDoobie }
import com.google.inject.AbstractModule
import crypto.{ HashService, MessageDigestHashService }
import daos._
import daos.doobie._
import format._

// import audits.EventDao

/**
 * User: Eduardo Barrientos
 * Date: 21/09/16
 * Time: 05:55 PM
 */
class DefaultBindings extends AbstractModule {

  def configure() = {
    bind(classOf[UserDao]) to classOf[UserDaoDoobie]
    bind(classOf[SubjectDao]) to classOf[SubjectDaoDoobie]
    bind(classOf[EventDao]) to classOf[EventDaoDoobie]
    bind(classOf[ItemDao]) to classOf[ItemDaoDoobie]
    bind(classOf[ClienteDao]) to classOf[ClienteDaoDoobie]
    bind(classOf[MascotaDao]) to classOf[MascotaDaoDoobie]
    bind(classOf[DateFormatter]) to classOf[YMDFormatter]

    bind(classOf[HashService]) toInstance MessageDigestHashService.Sha256HashService
  }
}
