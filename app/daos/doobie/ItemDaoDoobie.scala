package daos.doobie

import doobie.imports._
import doobie.util.transactor.DataSourceTransactor
import javax.inject.Inject
import play.api.db.Database
import play.api.Logger
import scala.math.BigDecimal
import daos.ItemDao
import models.{ Cliente, Item }

/**
 * Implementacion de DaoItem que utiliza Doobie
 * Obtiene su propio transactor.
 */
class ItemDaoDoobie @Inject() (db: Database) extends ItemDao {
  import DaoItemDoobie._

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

  def add(idCliente: Long, monto: BigDecimal, descripcion: String): Unit = {
    val updated = qAdd(idCliente, monto, descripcion).run.transact(transactor).unsafePerformIO
    Logger.debug(s"Item($idCliente, $monto), updated = $updated")
  }

  def datosCliente(idCliente: Long): Option[(String, List[Item])] = {
    val data: List[(String, Item)] = qDatosCliente(idCliente).list.transact(transactor).unsafePerformIO
    if (data.isEmpty) {
      None
    } else {
      Some(data(0)._1, data.map { case (_, i) ⇒ i })
    }
  }
}

object DaoItemDoobie {
  def qAdd(idCliente: Long, monto: BigDecimal, descripcion: String) =
    sql"""Insert into item(id_cliente, monto, descripcion)
          values ($idCliente, $monto, $descripcion)""".update

  def qDatosCliente(id: Long) =
    sql"""select c.nombre, i.*
          from item i join clientes c on i.id_cliente = c.id
          where c.id = $id""".query[(String, Item)]
}
