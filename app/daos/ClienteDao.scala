package daos

import models.{ Cliente, Telefono }

trait ClienteDao {
  /** Agregar un nuevo cliente */
  def addCliente(cliente: Cliente): Unit

  /** Todos los clientes */
  def clientes(): List[Cliente]

  /** Por cada cliente su id, nombre y saldo */
  def clientesSaldo(): List[(Cliente, BigDecimal)]

  /** Obtener un cliente por su ID */
  def byId(id: Long): Option[Cliente]

  /** Cliente por ID, asumiendo que 100% seguro existe */
  def unsafeById(id: Long): Cliente

  /** Agregar un telefono. El telefono contiene el id del cliente */
  def addTelf(t: Telefono): Unit

  /** Cliente por el id de una de sus mascotas */
  def byMascota(idMascota: Long): Option[Cliente]
}
