package daos.doobie

import daos.ServicioDao
import javax.inject.Inject
import doobie.imports._
import daos.doobie.DoobieImports._
import models.Servicio
import play.api.db.Database
import play.api.Logger

/** Implementar ServicioDao usando Doobie */
class ServicioDaoDoobie @Inject() (
    db: Database
) extends ServicioDao {
  import ServicioDaoDoobie._

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

  def registrar(nombre: String, costo: BigDecimal, mensual: Boolean): Unit = {
    Logger.debug(s"Registrar servicio: $nombre: $costo (mensual=$mensual)")

    qReg(nombre, costo, mensual).run.transact(transactor).unsafePerformIO
  }

  def todos: List[Servicio] = qServicios.list.transact(transactor).unsafePerformIO

  def byId(id: Long): Option[Servicio] =
    qById(id).option.transact(transactor).unsafePerformIO

  def actualizar(servicio: Servicio): Unit = {
    val updated = qActualizarServicio(servicio.id, servicio.nombre, servicio.precio, servicio.mensual)
      .run
      .transact(transactor)
      .unsafePerformIO
    Logger.debug(s"""Servicio "${servicio.nombre}" actualizado (updated = $updated)""")
  }
}

object ServicioDaoDoobie {
  def qReg(nombre: String, precio: BigDecimal, mensual: Boolean) =
    sql"""insert into servicio(nombre, precio, mensual)
          values($nombre, $precio, $mensual)""".update

  /** Fragmento para seleccionar un servicio sin filtro */
  private[this] def serv = fr"""select id, nombre, precio, mensual from servicio """

  def qServicios = serv.query[Servicio]

  def qById(id: Long) = (serv ++ sql"""where id = $id""").query[Servicio]

  def qActualizarServicio(id: Long, nombre: String, precio: BigDecimal, mensual: Boolean) =
    sql"""update servicio set nombre  = $nombre,
                              precio  = $precio,
                              mensual = $mensual
                          where id = $id""".update
}
