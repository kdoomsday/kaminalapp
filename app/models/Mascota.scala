package models

import org.joda.time.DateTime

/** Una mascota de un cliente */
case class Mascota(
  id: Long,
  nombre: String,
  raza: Option[String],
  edad: Option[Int],
  fechaInicio: Option[DateTime],
  idCliente: Long
)
