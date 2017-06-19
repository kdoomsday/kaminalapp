package models

import org.joda.time.Instant


/** Una mascota de un cliente */
case class Mascota(
  id: Long,
  nombre: String,
  raza: String,
  edad: Int,
  fechaInicio: Option[Instant]
)
