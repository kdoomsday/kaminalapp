package daos.doobie

import daos.ClienteDao
import javax.inject.Inject
import play.api.db.Database
import doobie.imports._
import models.Cliente
import play.api.Logger

// Implementacion de ClienteDao usando Doobie
class ClienteDaoDoobie @Inject() (db: Database) extends ClienteDao {
  import ClienteDaoDoobie._

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

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

object ClienteDaoDoobie {
  def qAddCliente(nombre: String) =
    sql"""Insert into clientes(nombre) values($nombre)""".update

  def qClientes() = sql"select id, nombre from clientes".query[Cliente]

  def qClientesSaldo() = sql"""select c.nombre, coalesce(sum(i.monto), 0) as saldo
                               from item i right outer join clientes c on i.id_cliente = c.id
                               group by c.nombre""".query[(String, BigDecimal)]
}
