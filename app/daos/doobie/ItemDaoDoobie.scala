package daos.doobie

import doobie.imports._
import doobie.util.transactor.DataSourceTransactor
import javax.inject.Inject
import play.api.db.Database
import play.api.Logger
import scala.math.BigDecimal
import daos.ItemDao
import models.Cliente

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

  def clientes(): List[Cliente] = {
    Logger.debug("Consulta de listado de clientes")
    qClientes().list.transact(transactor).unsafePerformIO
  }

  def clientesSaldo(): List[(String, BigDecimal)] = {
    Logger.debug("Consulta de saldo de los clientes")
    qClientesSaldo().list.transact(transactor).unsafePerformIO
  }
}

object DaoItemDoobie {
  def qAdd(idCliente: Long, monto: BigDecimal) =
    sql"""Insert into item(id_cliente, monto)
          values ($idCliente, $monto)""".update

  def qAddCliente(nombre: String) =
    sql"""Insert into clientes(nombre) values($nombre)""".update

  def qClientes() = sql"select id, nombre from clientes".query[Cliente]

  def qClientesSaldo() = sql"""select c.nombre, sum(i.monto)
                               from item i join clientes c on i.id_cliente = c.id
                               group by c.nombre""".query[(String, BigDecimal)]
}
