package daos

/** Dao para todo lo que tenga que ver con servicios */
trait ServicioDao {
  /** Registrar un nuevo servicio */
  def registrar(nombre: String, precio: BigDecimal, mensual: Boolean): Unit
}
