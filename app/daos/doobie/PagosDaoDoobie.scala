package daos.doobie

import doobie.imports._
import doobie.util.transactor.DataSourceTransactor
import daos.doobie.DoobieImports._
import javax.inject.Inject
import daos.PagosDao
import play.api.db.Database
import models.{ Cliente, Item, Mascota, PagoPendiente }
import play.api.Logger
import org.joda.time.DateTime

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

  def confirmarPago(idPago: Long): Item = {
    Logger.debug(s"Confirmar pago $idPago")

    val q = (for {
      p ← gFindPago(idPago).query[PagoPendiente].unique
      _ ← elimPago(p.id).update.run
      id ← qAddItem(p.idMascota, -p.monto, "Pago confirmado").update.withUniqueGeneratedKeys[Long]("id")
    } yield (id, p.idMascota, p.monto))

    val (id, idMascota, monto) = q.transact(transactor).unsafePerformIO
    Item(id, idMascota, monto, Some(""), new DateTime())
  }
}

object PagosDaoDoobie {
  def gFindPago(id: Long) = sql"""select * from pago_pendiente where id = $id"""

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

  def elimPago(id: Long) = sql"""delete from pago_pendiente where id=$id"""

  def qAddItem(idMascota: Long, monto: BigDecimal, descripcion: String) =
    sql"""Insert into item(id_mascota, monto, descripcion)
          values ($idMascota, $monto, $descripcion)"""

}
