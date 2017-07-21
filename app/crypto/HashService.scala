package crypto

/**
 * User: Eduardo Barrientos
 * Date: 15/10/16
 * Time: 05:23 PM
 */
trait HashService {

  /**
   * @param s    Plaintext String
   * @param salt Salt to use for hashing
   * @return Bytes produced
   */
  def hash(s: String, salt: Int): Array[Byte]

  /**
   * Hash de la cadena usando el salt especificado, convertido en una
   * cadena hex. Pasa la cadena por el método hash y el resultado por
   * HashService.toHexString
   */
  final def hashString(s: String, salt: Int): String = HashService.toHexString(hash(s, salt))

  /**
   * Automáticamente genera un salt y produce el hash del string
   * @param s Cadena en texto plano a la que se generará el hash
   * @return  La cadena procesada con hashString, junto con el salt utilizado.
   *          El salt es generado automáticamente
   */
  final def saltAndHashString(s: String): (String, Int) = {
    val salt = scala.util.Random.nextInt()
    hashString(s, salt) → salt
  }
}

object HashService {
  /** Convert an array of bytes to a corresponding hex string */
  def toHexString(bytes: Array[Byte]): String = {
    (for {
      b ← bytes
      hex = (b & 0xFF) + 0x100
      hexed = Integer.toHexString(hex).tail
    } yield hexed)
      .mkString
  }
}
