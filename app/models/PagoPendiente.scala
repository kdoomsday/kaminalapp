package models

/** Un pago registrado por el cliente que aún no ha sido confirmado */
case class PagoPendiente(
  id: Long,
  idMascota: Long,
  monto: BigDecimal,
  confirmacion: Option[String],
  imagen: Option[String]
)
