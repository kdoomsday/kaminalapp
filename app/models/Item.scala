package models

import java.sql.Timestamp

/** Un Item en el sistema */
case class Item(
  id: Long,
  idCliente: Long,
  monto: BigDecimal,
  descripcion: Option[String],
  fecha: Timestamp
)
