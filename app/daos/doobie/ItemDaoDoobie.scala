package daos.doobie

import doobie.imports._
import doobie.util.transactor.DataSourceTransactor
import javax.inject.Inject
import play.api.db.Database
import play.api.Logger
import scala.math.BigDecimal
import daos.ItemDao

/**
 * Implementacion de DaoItem que utiliza Doobie
 * Obtiene su propio transactor.
 */
class ItemDaoDoobie @Inject() (db: Database) extends ItemDao {
  import DaoItemDoobie._

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

  def add(idCliente: Long, monto: BigDecimal): Unit = {
    val updated = qAdd(idCliente, monto).run.transact(transactor).unsafePerformIO
    Logger.debug(s"Item($idCliente, $monto), updated = $updated")
  }

  def addCliente(nombre: String): Unit = {
    val up = qAddCliente(nombre).run.transact(transactor).unsafePerformIO
    Logger.debug(s"Nuevo cliente: $nombre, updated = $up")
  }
}

object DaoItemDoobie {
  def qAdd(idCliente: Long, monto: BigDecimal) =
    sql"""Insert into item(id_cliente, monto)
          values ($idCliente, $monto)""".update

  def qAddCliente(nombre: String) =
    sql"""Insert into clientes(nombre) values($nombre)""".update
}