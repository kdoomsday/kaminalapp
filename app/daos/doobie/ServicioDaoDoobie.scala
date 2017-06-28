package daos.doobie

import daos.ServicioDao
import javax.inject.Inject
import doobie.imports._
import daos.doobie.DoobieImports._
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
}

object ServicioDaoDoobie {
  def qReg(nombre: String, precio: BigDecimal, mensual: Boolean) =
    sql"""insert into servicio(nombre, precio, mensual)
          values($nombre, $precio, $mensual)""".update
}
