package daos.doobie

import org.specs2.mutable.Specification
import doobie.specs2.imports._
import testutil.TestUtil

class TestuserDaoDoobie extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(UserDaoDoobie.userIdQuery(0L))
}
