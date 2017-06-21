package daos

import models.Cliente

trait ClienteDao {
  /** Agregar un nuevo cliente */
  def addCliente(cliente: Cliente): Unit

  /** Todos los clientes */
  def clientes(): List[Cliente]

  /** Por cada cliente su id, nombre y saldo */
  def clientesSaldo(): List[(Long, String, BigDecimal)]

  /** Obtener un cliente por su ID */
  def byId(id: Long): Option[Cliente]

  /** Cliente por ID, asumiendo que 100% seguro existe */
  def unsafeById(id: Long): Cliente
}
