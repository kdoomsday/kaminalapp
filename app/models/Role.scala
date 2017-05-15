package models

import be.objectify.deadbolt.scala.models.{ Role ⇒ DeadboltRole }

/** Database role */
case class Role(
  id: Int,
  override val name: String
) extends DeadboltRole
