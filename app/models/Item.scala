package models

import org.joda.time.DateTime

/** Un Item en el sistema */
case class Item(
  id: Long,
  idCliente: Long,
  monto: BigDecimal,
  descripcion: Option[String],
  fecha: DateTime
)
