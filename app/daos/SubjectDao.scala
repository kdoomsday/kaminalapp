package daos

import models.security.MySubject

/** Dao for finding subjects. This is used mostly for security checks */
trait SubjectDao {
  /** Find subject by it's identifier */
  def subjectByIdentifier(identifier: String): Option[MySubject]
}
