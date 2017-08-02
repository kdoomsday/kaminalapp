package daos

import models.PagoPendiente

/** Dao para los pagos y pagos pendientes */
trait PagosDao {
  def pendiente(p: PagoPendiente): Unit
}
