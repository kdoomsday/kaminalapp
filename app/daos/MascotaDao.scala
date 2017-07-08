package daos

import models.{ Cliente, Mascota }

/** Persistencia de Mascotas */
trait MascotaDao {
  /** Guardar una mascota en BD, asociada a un cliente */
  def guardar(m: Mascota): Unit

  /** Buscar mascota seg&uacute;n su ID. Puede que no consiga resultado */
  def byId(idMascota: Long): Option[Mascota]

  /** Obtener una mascota con su cliente, por id de la mascota */
  def byIdConCliente(idMascota: Long): Option[(Mascota, Cliente)]

  /** Guardar los datos de una mascota que ya existe */
  def editar(m: Mascota): Unit
}
