package daos

import models.Mascota
import scala.math.BigDecimal
import models.{ Cliente, Item, Telefono }

/** Dao para operaciones de Items */
trait ItemDao {
  /** Agregar un item */
  def add(idMascota: Long, monto: BigDecimal, descripcion: String): Unit

  /**
   * Obtener todos los datos de un cliente seg&uacute;n su id
   * @return El cliente con todas sus mascotas, telefonos, e items
   * especificado, si el cliente existe
   */
  def datosCliente(idCliente: Long): Option[(Cliente, List[Mascota], List[Telefono], List[Item])]

  /**
   * Eliminar un item del sistema
   * @param  idItem Identificador del item a eliminar
   * @return Si un item fue, en efecto, eliminado
   */
  def eliminar(idItem: Long): Boolean
}
