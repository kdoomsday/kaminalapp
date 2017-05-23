package audits

import doobie.specs2.imports.AnalysisSpec
import org.specs2.mutable.Specification
import testutil.TestUtil

/** Pruebas de EventDaoDoobie */
object EventDaoDoobieSpec extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(EventDaoDoobie.qWrite(""))
}
