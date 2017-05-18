package bindings

import com.google.inject.AbstractModule
import javax.inject.{ Inject, Singleton }
import net.sf.ehcache.CacheManager
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/** Arreglar el problema de la instancia de EHCache ya inicializada */
class CacheFix extends AbstractModule {

  def configure() = {
    bind(classOf[CacheFixInstance]).asEagerSingleton()
  }
}

@Singleton
class CacheFixInstance @Inject() (
    lifecycle: ApplicationLifecycle
) {
  lifecycle.addStopHook { () ⇒
    Future(CacheManager.getInstance.shutdown())
  }
}
