package daos.doobie

import daos.MascotaDao
import javax.inject.Inject
import models.Mascota
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
}

object MascotaDaoDoobie {
  def qGuardarMascota(
    nombre: String,
    raza: Option[String],
    edad: Option[Int],
    fechaInicio: Option[DateTime],
    idCliente: Long
  ) =
    sql"""Insert into mascotas(nombre, raza, edad, fecha_inicio, id_cliente)
          values($nombre, $raza, $edad, $fechaInicio, $idCliente)""".update
}
