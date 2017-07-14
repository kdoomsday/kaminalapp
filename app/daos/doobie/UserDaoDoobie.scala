package daos.doobie

import daos.UserDao
import javax.inject.Inject
import models.User
import doobie.imports._
import play.api.db.Database
import play.api.Logger
import daos.doobie.DoobieImports._
import scala.util.Try

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

  def byId(id: Long): Option[User] = {
    Logger.debug(s"Consulta de usuario por id: $id")
    userIdQuery(id).option.transact(xa()).unsafePerformIO
  }

  def byLogin(login: String): Option[User] = {
    Logger.debug(s"Consulta de usuario por login: $login")
    userLoginQuery(login).option.transact(xa()).unsafePerformIO
  }

  def updateConnected(login: String): Unit = {
    val updated = setConnected(login).run.transact(xa()).unsafePerformIO
    Logger.debug(s"$updated usuarios marcados como conectados")
  }

  def usuariosInternos(): List[User] = {
    val usuarios = qUsersByRole("interno").list.transact(xa()).unsafePerformIO
    Logger.debug(s"Consulta de usuarios internos: ${usuarios.size} usuarios")
    usuarios
  }

  def crearUsuarioInterno(login: String, clave: String, salt: Int): Unit = {
    qCrearUsuarioInterno(login, clave, salt, "interno").run.transact(xa()).unsafePerformIO
  }

  def actualizarClave(idUsuario: Long, nuevaClave: String): Unit = {
    val up = qCambiarClave(idUsuario, nuevaClave)
               .run.transact(transactor).unsafePerformIO
    Logger.debug(s"Cambio de clave de usuario $idUsuario (updated=$up)")
  }
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

  def qUsersByRole(rolename: String) = sql"""select u.*
                                             from users u join roles r on u.role_id = r.id
                                             where r.name = $rolename""".query[User]

  def qCrearUsuarioInterno(
    login: String,
    clave: String,
    salt: Int,
    rolename: String
  ) =
    sql"""INSERT INTO users(login, password, role_id, salt)
          VALUES($login, $clave, (select id from roles where "name" = $rolename), $salt);""".update

  def qCambiarClave(idUsuario: Long, nuevaClave: String) =
    sql"""UPDATE users set password=$nuevaClave where id = $idUsuario""".update
}
