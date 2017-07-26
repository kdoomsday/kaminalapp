package resources

import models.ImageBlock

/** Cargador de imageBlocks */
trait ImageBlockLoader {
  def load(): ImageBlock
}
