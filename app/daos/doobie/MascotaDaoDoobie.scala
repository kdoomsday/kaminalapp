package daos.doobie

import daos.MascotaDao
import models.Mascota

/** Implementar MascotaDao usando Doobie */
class MascotaDaoDoobie extends MascotaDao {

}

object MascotaDaoDoobie {
  def qGuardarMascota(m: Mascota, idCliente: Long) =
    sql"""Insert into mas""".update
}
