package forms

import play.api.data.format.Formatter
import play.api.data.FormError
import play.api.data.Forms.of

/** Diversos formatters que se pueden utilizar para obtener datos de un form */
object FormFormatters {

  /** Formatter para crear el binder que se usa dentro del mapeo del form */
  private val cuentaFormat: Formatter[String] = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = {
      val text = data.getOrElse(key, "")
      if (text.matches("[0-9]{20}"))
        Right(text)
      else
        Left(List(FormError(key, "error.cuentaInvalida")))
    }

    def unbind(key: String, value: String) = Map(key → value)
  }

  /** Binder de un valor que deberia ser una cuenta */
  val cuenta = of(cuentaFormat)
}
