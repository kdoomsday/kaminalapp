package daos.doobie

import doobie.specs2.imports.AnalysisSpec
import org.specs2.mutable.Specification
import testutil.TestUtil
import PagosDaoDoobie._

object PagosDaoDoobieSpec extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(qAddPago(0L, BigDecimal("0"), None))
  check(qTodosPendientes)
}
