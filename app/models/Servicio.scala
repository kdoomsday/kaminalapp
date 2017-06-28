package models

/**
 * Representa un servicio prestado a un cliente
 * @param id      Identificador del servicio
 * @param nombre  Nombre o descripci&oacute;n
 * @param precio  Precio actual del servicio
 * @param mensual Si el servicio es mensual o no
 */
case class Servicio(
  id: Long,
  nombre: String,
  precio: BigDecimal,
  mensual: Boolean = false
)
