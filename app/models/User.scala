package models

import org.joda.time.Instant

/**
 * A user in the system
 * @param id    Database identifier
 * @param login User's login
 */
case class User(
  id: Long,
  login: String,
  password: String,
  salt: Int,
  roleId: Int,
  connected: Boolean,
  lastActivity: Option[Instant],
  cambioClave: Boolean
)
