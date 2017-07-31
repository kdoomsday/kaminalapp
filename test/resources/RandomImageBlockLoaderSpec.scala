package resources

import org.specs2.mutable.Specification

/** Pruebas unitarias del loader de imágenes */
class RandomImageBlockLoaderSpec extends Specification {

  "RandomImageBlockLoader" should {
    "Load three images even if there are less" in {
      // Espero que en este dir haya menos de 3 archivos para poder hacer la prueba
      val dir = "test/resources"
      val loader = new RandomImageBlockLoader(dir, dir)

      // Revisar que el directorio en efecto tenga menos de 3 porque si no
      // la prueba está rota
      val dirFile = new java.io.File(dir)
      val block = loader.load()

      dirFile.list.size < 3 &&
        block.first != null
    }
  }
}
