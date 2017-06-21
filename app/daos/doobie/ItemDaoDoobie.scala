package daos.doobie

import doobie.imports._
import doobie.util.transactor.DataSourceTransactor
import javax.inject.Inject
import models.Mascota
import play.api.db.Database
import play.api.Logger
import scala.math.BigDecimal
import daos.ItemDao
import daos.doobie.DoobieImports._
import models.{ Cliente, Item }

/**
 * Implementacion de DaoItem que utiliza Doobie
 * Obtiene su propio transactor.
 */
class ItemDaoDoobie @Inject() (db: Database) extends ItemDao {
  import DaoItemDoobie._

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

  def add(idCliente: Long, monto: BigDecimal, descripcion: String): Unit = {
    val updated = qAdd(idCliente, monto, descripcion).run.transact(transactor).unsafePerformIO
    Logger.debug(s"Item($idCliente, $monto), updated = $updated")
  }

  def datosCliente(idCliente: Long): Option[(String, List[Mascota], List[Item])] = {
    val q: (List[(String, Item)], List[Mascota]) = (for {
      datos ← qDatosCliente(idCliente).list.transact(transactor)
      mascotas ← qMascotas(idCliente).list.transact(transactor)
    } yield (datos, mascotas)).unsafePerformIO

    val (data, mascotas) = q

    if (data.isEmpty) {
      None
    } else {
      Some((data(0)._1, mascotas, data.map { case (_, i) ⇒ i }))
    }
  }
}

object DaoItemDoobie {
  def qAdd(idCliente: Long, monto: BigDecimal, descripcion: String) =
    sql"""Insert into item(id_cliente, monto, descripcion)
          values ($idCliente, $monto, $descripcion)""".update

  def qDatosCliente(id: Long) =
    sql"""select c.nombre, i.*
          from item i join clientes c on i.id_cliente = c.id
          where c.id = $id""".query[(String, Item)]

  def qMascotas(idcliente: Long) =
    sql"""select id, nombre, raza, edad, fecha_inicio, id_cliente
          from mascotas
          where id_cliente = $idcliente""".query[Mascota]
}
