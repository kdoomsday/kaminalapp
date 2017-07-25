package daos

import models.{ Role, User }
import scala.util.Try

/**
 * Dao for user-related queries
 * User: Eduardo Barrientos
 * Date: 20/09/16
 * Time: 04:24 PM
 */
trait UserDao {

  /** Buscar usuario por ID */
  def byId(id: Long): Option[User]

  /** Buscar usuario por login */
  def byLogin(login: String): Option[User]

  /** Buscar el usuario por login, trayendo también el rol que le corresponde */
  def byLoginWithRole(login: String): Option[(User, Role)]

  /**
   * Update the user's last connected time (also marks it connected)
   */
  def updateConnected(login: String): Unit

  /** Todos los usuarios internos del sistema */
  def usuariosInternos(): List[User]

  /**
   * Crear un nuevo usuario interno.
   * @param  login Login del usuario
   * @param  clave Clave del usuario ya procesada con salt y demas.
   * @param  salt  Salt utilizado para la clave
   */
  def crearUsuario(login: String, clave: String, salt: Int): Unit

  /** Actualizar la clave del usuario. La clave es persistida as-is */
  def actualizarClave(idUsuario: Long, nuevaClave: String, nuevoSalt: Int): Unit
}
