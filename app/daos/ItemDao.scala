package daos

import scala.math.BigDecimal
import models.Cliente

/** Dao para operaciones de Items */
trait ItemDao {

  /** Agregar un item */
  def add(idCliente: Long, monto: BigDecimal): Unit

  /** Agregar un nuevo cliente */
  def addCliente(nombre: String): Unit

  /** Todos los clientes */
  def clientes(): List[Cliente]
}
