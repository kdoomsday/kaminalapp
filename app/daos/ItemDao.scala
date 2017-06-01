package daos

import scala.math.BigDecimal
import models.Cliente

/** Dao para operaciones de Items */
trait ItemDao {
  /** Agregar un item */
  def add(idCliente: Long, monto: BigDecimal, descripcion: String): Unit
}
