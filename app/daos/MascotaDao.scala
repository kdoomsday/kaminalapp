package daos

import models.Mascota

/** Persistencia de Mascotas */
trait MascotaDao {
  /** Guardar una mascota en BD, asociada a un cliente */
  def guardar(m: Mascota): Unit
}
