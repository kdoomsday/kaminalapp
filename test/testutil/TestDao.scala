package testutil

import javax.inject.Inject
import models.User
import play.api.db.Database
import doobie.imports._
import daos.doobie.DoobieImports._

class TestDao @Inject() (db: Database) {
  val xa = TestUtil.transactor()

  private[this] def roleId(name: String): Int = {
    sql"select id from roles where name = $name".query[Int].option.transact(xa).unsafePerformIO.getOrElse(1)
  }

  // Ids de los roles
  lazy val idInterno = roleId("interno")
  lazy val idUsuario = roleId("usuario")

  // Usuario creado con valores por defecto
  lazy val testUser = User(
    id = 0,
    login = "prueba",
    password = "",
    salt = 0,
    roleId = idInterno,
    connected = false,
    lastActivity = None,
    cambioClave = false
  )

  /** Crear un usuario en la bd */
  def crearUsuario(u: User): User = {
    val q = sql"""insert into users(login, password, salt, role_id, connected, last_activity)
                  values(${u.login}, ${u.password}, ${u.salt}, ${u.roleId}, ${u.connected}, ${u.lastActivity})
               """.update.withUniqueGeneratedKeys[Long]("id")
    u.copy(id = q.transact(xa).unsafePerformIO)
  }

  /** Eliminar un usuario de la bd */
  def eliminarUsuario(login: String): Unit = {
    sql"delete from users where login = $login".update.run.transact(xa).unsafePerformIO
  }
}
