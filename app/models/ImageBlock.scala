package models

/** Bloque de tres imágenes para mostrar */
case class ImageBlock(
  first: String,
  second: String,
  third: String
)

object ImageBlock {
  def apply(l: List[String]): ImageBlock = {
    require(l.size >= 3)
    ImageBlock(
      l(0),
      l(1),
      l(2)
    )
  }
}
