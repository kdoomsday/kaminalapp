package json

import play.api.libs.json._

/**
 * Utilidades de ayuda para manipular objetos Json como respuesta a llamados
 * hechos a trav&eacute;s de la librer&iacute;a ajaxcall.js
 */
object JsonHelpers {

  /**
   * Respuesta de ok para un llamado ajax. Tiene siempre una clave "ok": "ok"
   * y puede, opcionalmente, enviar datos adicionales
   */
  def jsonOk(extras: (String, String)*) = {
    val extraArr = extras.map { case (k, v) ⇒ Json.obj(k → v) }
    extraArr.foldLeft(Json.obj("ok" → "ok")) { (acc, e) ⇒ acc ++ e }
  }

  /**
   * Crear un objeto json que representa un error para ajaxcall
   * @param data Data pasada como error en forma de json
   */
  def jsonErr(data: String) = Json.parse(s"""{ "data": "$data" }""")
}
