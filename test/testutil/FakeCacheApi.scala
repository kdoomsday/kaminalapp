package testutil

import play.api.cache.CacheApi
import scala.collection.mutable.{ Map ⇒ MMap }

/** Fake cache para evitar problemas en los tests */
class FakeCacheApi extends CacheApi {
  private[this] def data: MMap[String, Any] = MMap()

  def get[T](key: String)(implicit evidence$2: scala.reflect.ClassTag[T]): Option[T] = data.get(key).asInstanceOf[Option[T]]

  def getOrElse[A](key: String, expiration: scala.concurrent.duration.Duration)(orElse: ⇒ A)(implicit evidence$1: scala.reflect.ClassTag[A]): A = {
    if (data.contains(key))
      data(key).asInstanceOf[A]
    else
      orElse
  }

  def remove(key: String): Unit = data.remove(key)

  def set(key: String, value: Any, expiration: scala.concurrent.duration.Duration): Unit = data(key) = value
}
