package daos

import models.{ Cliente, Item, Mascota, PagoPendiente }

/** Dao para los pagos y pagos pendientes */
trait PagosDao {
  def addPendiente(p: PagoPendiente): Unit

  /**
   * Buscar todos los pagos pendientes junto con su mascota y el cliente
   * dueño de la mascota
   */
  def verPendientes(): List[(PagoPendiente, Mascota, Cliente)]

  /**
   * Confirmar un pago como listo. Lo elimina de la lista de los pendientes
   * y lo agrega a items
   * @return El item creado
   */
  def confirmarPago(idPago: Long): Item
}
