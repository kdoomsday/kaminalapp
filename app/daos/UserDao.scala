package daos

import models.User
import scala.util.Try

/**
 * Dao for user-related queries
 * User: Eduardo Barrientos
 * Date: 20/09/16
 * Time: 04:24 PM
 */
trait UserDao {

  def byId(id: Long): Option[User]
  def byLogin(login: String): Option[User]

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
  def crearUsuarioInterno(login: String, clave: String, salt: Int): Unit
}
