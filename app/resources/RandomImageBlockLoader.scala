package resources

import java.nio.file.{ Files, Paths }

import scala.collection.JavaConversions._

import models.ImageBlock

/** Carga el imageBlock aleatoriamente de entre los archivos de una ruta */
class RandomImageBlockLoader(
    basePath: String,
    baseLoad: String
) extends ImageBlockLoader {

  private[this] val imgsPerBlock = 4

  // El constructor de zero args usa los valores por defecto
  def this() = this("public/images/display/", "external_assets/display/")

  def load(): ImageBlock = {
    // Cargar todos los archivos
    def loadBasic(): List[String] = {
      val path = Paths.get(basePath)
      if (Files.exists(path)) {
        Files.walk(path)
          .iterator()
          .filter(f ⇒ !Files.isDirectory(f))
          .map(_.getFileName.toString)
          .toList
      } else
        Nil
    }

    // Seleccionar los archivos que queremos
    def select(init: List[String]): List[String] =
      scala.util.Random.shuffle(init).take(imgsPerBlock)

    // Que usar si faltan imágenes
    def defaultImage = baseLoad + "assets/images/knlogo.png"

    // Rellenar en caso de que no hayan suficientes imágenes
    def fill(have: List[String], n: Int): List[String] = {
      if (have.size >= n) have
      else {
        val amnt = n - have.size
        val base = defaultImage
        have ++ (List.fill(amnt)(base))
      }
    }

    val files = select(loadBasic()).map(baseLoad + _)
    ImageBlock(fill(files, imgsPerBlock))
  }

}
