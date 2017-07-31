package controllers

import play.api.mvc.{ Action, Controller }
import java.io.File

/** Controlador para poder servir archivos externos a los empaquetados */
class ExternalAssetsController extends Controller {

  private[this] val parent = new File("public/images")

  /** Cargar archivo en la ruta base + path */
  def at(path: String) = Action {
    val file = new File(parent, path)
    Ok.sendFile(file, true)
  }
}
