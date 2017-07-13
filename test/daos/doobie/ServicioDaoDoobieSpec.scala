package daos.doobie

import doobie.specs2.imports.AnalysisSpec
import org.specs2.mutable.Specification
import testutil.TestUtil

object ServicioDaoDoobieSpec extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(ServicioDaoDoobie.qReg("", BigDecimal(0), false))
  check(ServicioDaoDoobie.qServicios)
  check(ServicioDaoDoobie.qById(0L))
}
