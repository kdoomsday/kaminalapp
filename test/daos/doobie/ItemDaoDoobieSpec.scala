package daos.doobie

import doobie.specs2.imports.AnalysisSpec
import org.specs2.mutable.Specification
import testutil.TestUtil
import scala.math.BigDecimal

object ItemDaoDoobieSpec extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(DaoItemDoobie.qAdd(0L, BigDecimal(0)))
}
