package format

import org.joda.time.{ DateTime, Instant }
import org.joda.time.format.{ DateTimeFormat, DateTimeFormatter }

/** Formateador de fechas de diversos tipos */
trait DateFormatter {
  // Formatear en formato corto (sin hora)
  def short(d: DateTime): String
  def short(i: Instant): String
  def short(o: Option[_]): String = o.map {
    case d: DateTime ⇒ short(d)
    case i: Instant  ⇒ short(i)
    case _           ⇒ ""
  }.getOrElse("")

  // Formatear en formato largo (con hora)
  def long(d: DateTime): String
  def long(i: Instant): String
  def long(o: Option[_]): String = o.map {
    case d: DateTime ⇒ long(d)
    case i: Instant  ⇒ long(i)
    case _           ⇒ ""
  }.getOrElse("")
}

/** Implementa DateFormatter con un formato de yyyy-MM-dd */
class YMDFormatter extends DateFormatter {
  private[this] val shortFmt = "yyyy-MM-dd"
  private[this] val longFmt = "yyyy-MM-dd HH:mm:ss"

  private[this] lazy val sf = DateTimeFormat.forPattern(shortFmt)
  private[this] lazy val lf = DateTimeFormat.forPattern(longFmt)

  // Formatear en formato corto (sin hora)
  def short(d: DateTime): String = sf.print(d)
  def short(i: Instant): String = sf.print(i)

  // Formatear en formato largo (con hora)
  def long(d: DateTime): String = lf.print(d)
  def long(i: Instant): String = lf.print(i)
}
