package daos.doobie

import doobie.specs2.imports._
import org.specs2.mutable.Specification
import testutil.TestUtil

/** Tests para SubjectDaoDoobie */
class TestSubjectDaoDoobie extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(SubjectDaoDoobie.subjectQuery("login"))
}
