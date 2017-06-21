package models

/** Un cliente del sistema */
case class Cliente(
    id: Long,
    nombre: String,
    apellido: String,
    direccion: Option[String],
    email: Option[String]
) {
  def nombreCompleto = s"$nombre $apellido"
}
