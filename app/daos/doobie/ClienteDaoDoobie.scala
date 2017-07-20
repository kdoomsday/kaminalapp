package daos.doobie

import daos.ClienteDao
import javax.inject.Inject
import play.api.db.Database
import doobie.imports._
import models.{ Cliente, Mascota, Telefono }
import play.api.Logger
import daos.doobie.DoobieImports._

// Implementacion de ClienteDao usando Doobie
class ClienteDaoDoobie @Inject() (db: Database) extends ClienteDao {
  import ClienteDaoDoobie._
  import MascotaDaoDoobie.qGuardarMascota

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

  def addCliente(c: Cliente, m: Mascota): Unit = {
    Logger.debug(s"Agregar nuevo cliente: ${c.nombre} ${c.apellido}")

    val query = (for {
      idCliente ← qAddCliente(c.nombre, c.apellido, c.direccion, c.email, c.cuenta).withUniqueGeneratedKeys[Long]("id")
      q ← qGuardarMascota(m.nombre, m.raza, m.edad, m.fechaInicio, idCliente).run
    } yield q)

    val updated = query.transact(transactor).unsafePerformIO
    Logger.debug(s"Cliente insertado (updated = $updated)")

    //qAddCliente(c.nombre, c.apellido, c.direccion, c.email, c.cuenta)
    //  .run
    //  .transact(transactor)
    //  .unsafePerformIO
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

  def addTelf(t: Telefono): Unit = {
    qAddTelefono(t.numero, t.idCliente).run.transact(transactor).unsafePerformIO
  }

  def byMascota(idMascota: Long): Option[Cliente] = {
    qByMascota(idMascota).option.transact(transactor).unsafePerformIO
  }

  def actualizarNombre(id: Long, nombre: String, apellido: String) = {
    val up = qUpdateNombre(id, nombre, apellido)
      .run
      .transact(transactor)
      .unsafePerformIO
    Logger.debug(s"Actualizar cliente $id, nombre = $nombre, apellido = $apellido (updated=$up)")
  }
}

object ClienteDaoDoobie {
  def qAddCliente(
    nombre: String,
    apellido: String,
    direccion: Option[String],
    email: String,
    cuenta: Option[String]
  ) =
    sql"""Insert into clientes(nombre, apellido, direccion, email, cuenta)
          values($nombre, $apellido, $direccion, $email, $cuenta)""".update

  def qClientesSaldo() = sql"""select c.*, coalesce(sum(i.monto), 0) as saldo
                               from item i right outer join mascotas m on i.id_mascota = m.id
                               right outer join clientes c on m.id_cliente = c.id
                               group by c.id""".query[(Cliente, BigDecimal)]

  private[this] val clientesFrag = sql"select id, nombre, apellido, direccion, email, cuenta from clientes "
  def qClientes() = clientesFrag.query[Cliente]
  def qById(id: Long) = (clientesFrag ++ fr"where id = $id").query[Cliente]

  def qByMascota(idMascota: Long) = sql"""select c.*
                                          from mascotas m join clientes c on m.id_cliente = c.id
                                          where m.id = $idMascota""".query[Cliente]

  def qAddTelefono(numero: String, idCliente: Long) =
    sql"""insert into telefonos(numero, id_cliente)
          values($numero, $idCliente)""".update

  def qUpdateNombre(id: Long, nombre: String, apellido: String) =
    sql"""update clientes set nombre = $nombre, apellido = $apellido
          where id = $id""".update
}
