package daos

import models.PagoPendiente

/** Dao para los pagos y pagos pendientes */
trait PagosDao {
  def addPendiente(p: PagoPendiente): Unit

  def verPendientes(): List[PagoPendiente]
}
