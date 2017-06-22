package daos

import models.Mascota
import scala.math.BigDecimal
import models.{ Cliente, Item }

/** Dao para operaciones de Items */
trait ItemDao {
  /** Agregar un item */
  def add(idCliente: Long, monto: BigDecimal, descripcion: String): Unit

  /**
   * Obtener todos los datos de un cliente seg&uacute;n su id
   * @return El cliente, todas las mascotas y todos los items del cliente
   * especificado, si el cliente existe
   */
  def datosCliente(idCliente: Long): Option[(Cliente, List[Mascota], List[Item])]
}
