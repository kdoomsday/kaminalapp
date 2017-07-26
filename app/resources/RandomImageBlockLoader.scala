package resources

import models.ImageBlock
import scala.collection.JavaConversions._
import java.io.File

/** Carga el imageBlock aleatoriamente de entre los archivos de una ruta */
class RandomImageBlockLoader extends ImageBlockLoader {

  // Ruta base de las imagenes
  private[this] lazy val basePath = "public/images/display/"
  private[this] lazy val baseLoad = "assets/images/display/"

  def load(): ImageBlock = {
    val path = new File(basePath)
    val files = scala.util.Random.shuffle(path.list().toList).take(3)

    ImageBlock(files map (f ⇒ baseLoad + f))
  }

}
