package daos

import models.{ Cliente, Mascota, PagoPendiente }

/** Dao para los pagos y pagos pendientes */
trait PagosDao {
  def addPendiente(p: PagoPendiente): Unit

  /**
   * Buscar todos los pagos pendientes junto con su mascota y el cliente
   * dueño de la mascota
   */
  def verPendientes(): List[(PagoPendiente, Mascota, Cliente)]
}
