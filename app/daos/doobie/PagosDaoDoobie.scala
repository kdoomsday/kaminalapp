package daos.doobie

import doobie.imports._
import doobie.util.transactor.DataSourceTransactor
import daos.doobie.DoobieImports._
import javax.inject.Inject
import daos.PagosDao
import play.api.db.Database
import models.{ Cliente, Mascota, PagoPendiente }
import play.api.Logger

/** Implementación de PagosDao con Doobie */
class PagosDaoDoobie @Inject() (db: Database) extends PagosDao {
  import PagosDaoDoobie._

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

  def addPendiente(p: PagoPendiente): Unit = {
    Logger.debug(s"Agregar pago pendiente para mascota ${p.idMascota}")
    qAddPago(p.idMascota, p.monto, p.confirmacion).run.transact(transactor).unsafePerformIO
  }

  def verPendientes(): List[(PagoPendiente, Mascota, Cliente)] = {
    Logger.debug(s"Consultar lista de pagos pendientes")
    qTodosPendientes.list.transact(transactor).unsafePerformIO
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

  val qTodosPendientes =
    sql"""select pp.*, m.*, c.*
          from pago_pendiente pp
	        join mascotas m on pp.id_mascota = m.id
	        join clientes c on m.id_cliente = c.id
       """.query[(PagoPendiente, Mascota, Cliente)]
}
