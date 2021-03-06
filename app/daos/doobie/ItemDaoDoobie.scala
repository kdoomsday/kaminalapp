package daos.doobie

import doobie.imports._
import doobie.util.transactor.DataSourceTransactor
import javax.inject.Inject
import models.Mascota
import org.joda.time.DateTime
import play.api.db.Database
import play.api.Logger
import scala.math.BigDecimal
import daos.ItemDao
import daos.doobie.DoobieImports._
import models.{ Cliente, Item, Telefono }

/**
 * Implementacion de DaoItem que utiliza Doobie
 * Obtiene su propio transactor.
 */
class ItemDaoDoobie @Inject() (db: Database) extends ItemDao {
  import DaoItemDoobie._

  val transactor = DataSourceTransactor[IOLite](db.dataSource)

  def add(idMascota: Long, monto: BigDecimal, descripcion: String): Unit = {
    val updated = qAdd(idMascota, monto, descripcion).run.transact(transactor).unsafePerformIO
    Logger.debug(s"Item($idMascota, $monto), updated = $updated")
  }

  def addByServicio(idMascota: Long, idServicio: Long): Unit = {
    val updated = qAddByServicio(idMascota, idServicio)
      .run
      .transact(transactor)
      .unsafePerformIO
    Logger.debug(s"Agregar servicio a mascota (mascota=$idMascota, servicio=$idServicio, updated=$updated)")
  }

  def datosCliente(idCliente: Long): Option[(Cliente, List[Mascota], List[Telefono], List[Item])] = {
    val q: ConnectionIO[(Option[Cliente], List[Item], List[Mascota], List[Telefono])] = for {
      c ← qCliente(idCliente).option
      datos ← qDatosCliente(idCliente).list
      mascotas ← qMascotas(idCliente).list
      telefonos ← qTelefonos(idCliente).list
    } yield (c, datos, mascotas, telefonos)

    val (cliente, data, mascotas, telefonos) = q.transact(transactor).unsafePerformIO

    cliente.map(c ⇒ (c, mascotas, telefonos, data))
  }

  def eliminar(idItem: Long): Boolean = {
    Logger.debug(s"Eliminar item con id $idItem")
    val updated = qEliminar(idItem).run.transact(transactor).unsafePerformIO
    if (updated > 1)
      Logger.warn("Eliminados demasiados items!")
    return updated > 0
  }

  def byId(idItem: Long): Option[(Item, Mascota, Cliente)] = {
    Logger.debug(s"Buscar item por id: $idItem")
    qById(idItem).option.transact(transactor).unsafePerformIO
  }

  def actualizar(idItem: Long, monto: BigDecimal, descripcion: String) = {
    val updated = qEditar(idItem, monto, descripcion).run.transact(transactor).unsafePerformIO
    Logger.debug(s"Actualizar item $idItem: $monto $descripcion (actualizados = $updated)")
  }
}

object DaoItemDoobie {
  def qAdd(idMascota: Long, monto: BigDecimal, descripcion: String) =
    sql"""Insert into item(id_mascota, monto, descripcion)
          values ($idMascota, $monto, $descripcion)""".update

  def qAddByServicio(idMascota: Long, idServicio: Long) =
    sql"""insert into item(monto, descripcion, id_mascota)
          select precio, nombre, $idMascota
          from servicio
          where id = $idServicio""".update

  def qCliente(id: Long) = sql"select * from clientes where id = $id".query[Cliente]

  def qDatosCliente(idCliente: Long) =
    sql"""select i.id, id_mascota, monto, descripcion, fecha
          from item i
            join mascotas m on i.id_mascota = m.id
            join clientes c on m.id_cliente = c.id
          where c.id = $idCliente
          order by fecha desc""".query[Item]

  def qMascotas(idcliente: Long) =
    sql"""select id, nombre, raza, edad, fecha_inicio, id_cliente
          from mascotas
          where id_cliente = $idcliente""".query[Mascota]

  def qTelefonos(idCliente: Long) =
    sql"""select numero, id_cliente
          from telefonos
          where id_cliente = $idCliente""".query[Telefono]

  def qEliminar(id: Long) = sql"""delete from item where id = $id""".update

  def qById(idItem: Long) =
    sql"""select i.id, i.id_mascota, i.monto, i.descripcion, i.fecha,
                 m.id, m.nombre, m.raza, m.edad, m.fecha_inicio, m.id_cliente,
                 c.id, c.nombre, c.apellido, c.direccion, c.email, c.cuenta
          from item i
            join mascotas m on i.id_mascota = m.id
            join clientes c on m.id_cliente = c.id
          where i.id = $idItem""".query[(Item, Mascota, Cliente)]

  def qEditar(idItem: Long, monto: BigDecimal, descripcion: String) =
    sql"""update item set monto=$monto, descripcion=$descripcion
          where id = $idItem""".update
}
