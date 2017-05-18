package daos.doobie

import daos.SubjectDao
import daos.doobie.DoobieImports._
import doobie.imports._
import doobie.util.transactor.DataSourceTransactor
import javax.inject.Inject
import models.{ Role, User }
import models.security.MySubject
import org.joda.time.Instant
import play.api.db.Database

class SubjectDaoDoobie @Inject() (
    db: Database
) extends SubjectDao {
  import SubjectDaoDoobie.subjectQuery

  private[this] implicit def xa() = DataSourceTransactor[IOLite](db.dataSource)

  def subjectByIdentifier(identifier: String): Option[MySubject] = {
    subjectQuery(identifier).option.transact(xa()).unsafePerformIO map {
      case (login, connected, lastAct, rid, rname) ⇒
        MySubject(
          User(0L, login, "", 0, rid, connected, lastAct),
          Role(rid, rname)
        )
    }
  }
}

object SubjectDaoDoobie {

  def subjectQuery(login: String): Query0[(String, Boolean, Option[Instant], Int, String)] =
    sql"""
      Select u.login, u.connected, u.last_activity, r.id, r.name
      from users u join roles r on u.role_id = r.id
      where u.login = $login""".query[(String, Boolean, Option[Instant], Int, String)]
}
