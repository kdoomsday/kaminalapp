package daos.doobie

import daos.ClienteDao
import javax.inject.Inject
import play.api.db.Database
import doobie.imports._
import models.Cliente
import play.api.Logger
import daos.doobie.DoobieImports._

// Implementacion de ClienteDao usando Doobie
class ClienteDaoDoobie @Inject() (db: Database) extends ClienteDao {
  import ClienteDaoDoobie._

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

  def addCliente(c: Cliente): Unit = {
    val up = qAddCliente(c.nombre, c.apellido, c.direccion, c.email, c.cuenta)
      .run
      .transact(transactor)
      .unsafePerformIO
    Logger.debug(s"Nuevo cliente: ${c.nombre} ${c.apellido}, updated = $up")
  }

  def clientes(): List[Cliente] = {
    Logger.debug("Consulta de listado de clientes")
    qClientes().list.transact(transactor).unsafePerformIO
  }

  def clientesSaldo(): List[(Cliente, BigDecimal)] = {
    Logger.debug("Consulta de saldo de los clientes")
    qClientesSaldo().list.transact(transactor).unsafePerformIO
  }

  def byId(id: Long): Option[Cliente] = {
    Logger.debug(s"Consulta de cliente por id ({$id})")
    qById(id).option.transact(transactor).unsafePerformIO
  }

  def unsafeById(id: Long): Cliente = {
    qById(id).unique.transact(transactor).unsafePerformIO
  }
}

object ClienteDaoDoobie {
  def qAddCliente(
    nombre: String,
    apellido: String,
    direccion: Option[String],
    email: Option[String],
    cuenta: Option[String]
  ) =
    sql"""Insert into clientes(nombre, apellido, direccion, email, cuenta)
          values($nombre, $apellido, $direccion, $email, $cuenta)""".update

  def qClientesSaldo() = sql"""select c.*, coalesce(sum(i.monto), 0) as saldo
                               from item i right outer join clientes c on i.id_cliente = c.id
                               group by c.id""".query[(Cliente, BigDecimal)]

  private[this] val clientesFrag = sql"select id, nombre, apellido, direccion, email, cuenta from clientes "
  def qClientes() = clientesFrag.query[Cliente]
  def qById(id: Long) = (clientesFrag ++ fr"where id = $id").query[Cliente]
}
