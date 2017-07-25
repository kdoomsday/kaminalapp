package daos.doobie

import daos.MascotaDao
import javax.inject.Inject
import models.{ Cliente, Mascota }
import org.joda.time.DateTime
import doobie.imports._
import daos.doobie.DoobieImports._
import play.api.Logger
import play.api.db.Database

/** Implementar MascotaDao usando Doobie */
class MascotaDaoDoobie @Inject() (db: Database) extends MascotaDao {
  import MascotaDaoDoobie._

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

  def guardar(m: Mascota) = {
    Logger.debug(s"Guardando mascota ${m.nombre} para cliente ${m.idCliente}")
    qGuardarMascota(m.nombre, m.raza, m.edad, m.fechaInicio, m.idCliente)
      .run
      .transact(transactor)
      .unsafePerformIO
  }

  def byId(idMascota: Long): Option[Mascota] = {
    Logger.debug(s"Buscar mascota por id = $idMascota")
    qById(idMascota)
      .option
      .transact(transactor)
      .unsafePerformIO
  }

  def byIdConCliente(idMascota: Long): Option[(Mascota, Cliente)] = {
    Logger.debug(s"Mascota con cliente por id = $idMascota")
    qMascotaConCliente(idMascota)
      .option
      .transact(transactor)
      .unsafePerformIO
  }

  def editar(m: Mascota): Unit = {
    Logger.debug(s"Editar datos de mascota ${m.nombre} (${m.id})")
    qEditMascota(m.id, m.nombre, m.raza, m.edad, m.fechaInicio)
      .run
      .transact(transactor)
      .unsafePerformIO
  }

  def mascotasCliente(login: String): List[Mascota] = {
    Logger.debug(s"Listado de mascotas de un cliente según su login")
    qByLoginCliente(login).list.transact(transactor).unsafePerformIO
  }
}

object MascotaDaoDoobie {
  private[this] val selMascota = fr"select m.id, m.nombre, m.raza, m.edad, m.fecha_inicio, m.id_cliente from mascotas m "

  def qGuardarMascota(
    nombre: String,
    raza: Option[String],
    edad: Option[Int],
    fechaInicio: Option[DateTime],
    idCliente: Long
  ) =
    sql"""Insert into mascotas(nombre, raza, edad, fecha_inicio, id_cliente)
          values($nombre, $raza, $edad, $fechaInicio, $idCliente)""".update

  def qById(id: Long) = (selMascota ++ fr"""where m.id = $id""").query[Mascota]

  def qEditMascota(
    idMascota: Long,
    nombre: String,
    raza: Option[String],
    edad: Option[Int],
    fechaInicio: Option[DateTime]
  ) = sql"""update mascotas set nombre = $nombre,
                               raza   = $raza,
                               edad   = $edad,
                               fecha_inicio = $fechaInicio
                           where id = $idMascota""".update

  def qMascotaConCliente(idMascota: Long) =
    sql"""select m.*, c.*
          from mascotas m join clientes c on m.id_cliente = c.id
          where m.id = $idMascota""".query[(Mascota, Cliente)]

  def qByLoginCliente(login: String) = (selMascota ++ fr"""join clientes c on m.id_cliente = c.id
                                                           where c.email = $login""").query[Mascota]
}
