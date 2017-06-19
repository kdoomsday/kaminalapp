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

  def addCliente(c: Cliente): Unit = {
    val up = qAddCliente(c.nombre, c.apellido, c.direccion, c.email)
      .run
      .transact(transactor)
      .unsafePerformIO
    Logger.debug(s"Nuevo cliente: ${c.nombre} ${c.apellido}, updated = $up")
  }

  def clientes(): List[Cliente] = {
    Logger.debug("Consulta de listado de clientes")
    qClientes().list.transact(transactor).unsafePerformIO
  }

  def clientesSaldo(): List[(Long, String, BigDecimal)] = {
    Logger.debug("Consulta de saldo de los clientes")
    qClientesSaldo().list.transact(transactor).unsafePerformIO
  }
}

object ClienteDaoDoobie {
  def qAddCliente(
    nombre: String,
    apellido: String,
    direccion: Option[String],
    email: Option[String]
  ) =
    sql"""Insert into clientes(nombre, apellido, direccion, email)
          values($nombre, $apellido, $direccion, $email)""".update

  def qClientes() = sql"select id, nombre, apellido, direccion, email from clientes".query[Cliente]

  def qClientesSaldo() = sql"""select c.id, c.nombre, coalesce(sum(i.monto), 0) as saldo
                               from item i right outer join clientes c on i.id_cliente = c.id
                               group by c.id, c.nombre""".query[(Long, String, BigDecimal)]
}