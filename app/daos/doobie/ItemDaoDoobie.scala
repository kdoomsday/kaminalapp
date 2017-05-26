package daos.doobie

import doobie.imports._
import doobie.util.transactor.DataSourceTransactor
import javax.inject.Inject
import play.api.db.Database
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
    qAdd(idCliente, monto).run.transact(transactor).unsafePerformIO
  }
}

object DaoItemDoobie {
  def qAdd(idCliente: Long, monto: BigDecimal) =
    sql"""Insert into item(id_cliente, monto)
          values ($idCliente, $monto)""".update
}
