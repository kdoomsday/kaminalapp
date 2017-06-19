package daos

import models.Cliente

trait ClienteDao {
  /** Agregar un nuevo cliente */
  def addCliente(cliente: Cliente): Unit

  /** Todos los clientes */
  def clientes(): List[Cliente]

  /** Por cada cliente su id, nombre y saldo */
  def clientesSaldo(): List[(Long, String, BigDecimal)]
}
