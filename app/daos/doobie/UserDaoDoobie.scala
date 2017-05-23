package daos.doobie

import daos.UserDao
import javax.inject.Inject
import models.User
import doobie.imports._
import play.api.db.Database
import daos.doobie.DoobieImports._

class UserDaoDoobie @Inject() (
    db: Database
) extends UserDao {
  import UserDaoDoobie._

  /**
   * Crea un transactor (por llamado). En teoria db deberia usar hikari,
   * de manera que deberia funcionar bien
   */
  // private[this] implicit def xa() = DataSourceTransactor[IOLite](db.dataSource)
  private[this] implicit def xa() = DoobieTransactor.transactor(db)

  def byId(id: Long): Option[User] =
    userIdQuery(id).option.transact(xa()).unsafePerformIO

  def byLogin(login: String): Option[User] =
    userLoginQuery(login).option.transact(xa()).unsafePerformIO

  def updateConnected(login: String): Unit =
    setConnected(login).run.transact(xa())
}

/** Los queries, para poder chequearlos */
object UserDaoDoobie {
  // Query para conseguir Option[User] por id
  def userIdQuery(id: Long): Query0[User] =
    sql"""Select id, login, password, salt, role_id, connected, last_activity
        from users where id=$id
     """.query[User]

  def userLoginQuery(login: String): Query0[User] =
    sql"""Select id, login, password, salt, role_id, connected, last_activity
          from users where login=$login
       """.query[User]

  def setConnected(login: String): Update0 =
    sql"""update users set connected=true, last_activity=now()
          where login = $login""".update
}
