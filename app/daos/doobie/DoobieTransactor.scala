package daos.doobie

import doobie.imports.IOLite
import doobie.util.transactor.DataSourceTransactor
import play.api.db.Database

/** Utilitario para obtener el transactor para la conexion a BD */
object DoobieTransactor {
  def transactor(db: Database) = DataSourceTransactor[IOLite](db.dataSource)
}
