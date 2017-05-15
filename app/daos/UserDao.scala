package daos

import models.User

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
   * @return If the operation was successful
   */
  def updateConnected(login: String): Boolean
}
