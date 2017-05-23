package daos.doobie

import doobie.specs2.imports.AnalysisSpec
import org.specs2.mutable.Specification
import testutil.TestUtil

/** Pruebas de SubjectDaoDoobie */
object SubjectDaoDoobieSpec extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(SubjectDaoDoobie.subjectQuery(""))
}
