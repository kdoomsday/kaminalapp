package daos

import models.Servicio

/** Dao para todo lo que tenga que ver con servicios */
trait ServicioDao {
  /** Registrar un nuevo servicio */
  def registrar(nombre: String, precio: BigDecimal, mensual: Boolean): Unit

  /** Todos los servicios */
  def todos: List[Servicio]

  /** Obtener el servicio por id */
  def byId(id: Long): Option[Servicio]

  /** Actualizar los datos de un servicio */
  def actualizar(servicio: Servicio): Unit
}
