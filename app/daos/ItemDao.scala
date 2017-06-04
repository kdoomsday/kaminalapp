package daos

import scala.math.BigDecimal
import models.{ Cliente, Item }

/** Dao para operaciones de Items */
trait ItemDao {
  /** Agregar un item */
  def add(idCliente: Long, monto: BigDecimal, descripcion: String): Unit

  /**
   * Obtener todos los datos de un cliente seg&uacute;n su id
   * @return El nombre del cliente y todos los items del cliente
   * especificado, si el cliente existe
   */
  def datosCliente(idCliente: Long): Option[(String, List[Item])]
}
