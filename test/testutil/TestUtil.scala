package testutil

import doobie.imports.IOLite
import doobie.util.transactor.DataSourceTransactor
import play.api.cache.CacheApi
import play.api.db.Database
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind

/** Utilitarios para ayudar con las pruebas */
object TestUtil {
  /** Injector para obtener los objetos de la aplicacion */
  lazy val injector = new GuiceApplicationBuilder().
    overrides(bind(classOf[CacheApi]) to (classOf[FakeCacheApi])).
    injector()

  /** Transactor para las pruebas de base de datos */
  def transactor() = DataSourceTransactor[IOLite](
    injector.instanceOf[Database].dataSource
  )

}
