package daos.doobie

import doobie.imports._
import doobie.util.transactor.DataSourceTransactor
import javax.inject.Inject
import daos.PagosDao
import play.api.db.Database
import models.PagoPendiente
import play.api.Logger

class PagosDaoDoobie @Inject() (db: Database) extends PagosDao {
  import PagosDaoDoobie._

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

  def addPendiente(p: PagoPendiente): Unit = {
    Logger.debug(s"Agregar pago pendiente para mascota ${p.idMascota}")
    qAddPago(p.idMascota, p.monto, p.confirmacion).run.transact(transactor).unsafePerformIO
  }
}

object PagosDaoDoobie {

  def qAddPago(
    idMascota: Long,
    monto: BigDecimal,
    confirmacion: Option[String]
  ) =
    sql"""insert into pago_pendiente(id_mascota, monto, confirmacion)
          values($idMascota, $monto, $confirmacion)""".update
}
