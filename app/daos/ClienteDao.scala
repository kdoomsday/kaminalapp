package daos

import models.Cliente

trait ClienteDao {
  /** Agregar un nuevo cliente */
  def addCliente(nombre: String): Unit

  /** Todos los clientes */
  def clientes(): List[Cliente]

  /** El nombre de cada cliente junto con su saldo */
  def clientesSaldo(): List[(String, BigDecimal)]
}
