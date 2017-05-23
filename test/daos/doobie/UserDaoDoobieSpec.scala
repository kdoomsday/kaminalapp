package daos.doobie

import doobie.specs2.imports.AnalysisSpec
import org.specs2.mutable.Specification
import testutil.TestUtil

/** Pruebas para los queries de UserDaoDoobie */
object UserDaoDoobieSpec extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(UserDaoDoobie.userIdQuery(0L))
  check(UserDaoDoobie.userLoginQuery(""))
  check(UserDaoDoobie.setConnected(""))
}
