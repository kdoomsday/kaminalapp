package daos.doobie

import doobie.specs2.imports.AnalysisSpec
import org.specs2.mutable.Specification
import testutil.TestUtil
import scala.math.BigDecimal

object ItemDaoDoobieSpec extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  import DaoItemDoobie._

  check(qAdd(0L, BigDecimal(0), ""))
  check(qAddByServicio(0L, 0L))
  check(qDatosCliente(0L))
  check(qMascotas(0L))
  check(qCliente(0L))
  check(qTelefonos(0L))
  check(qById(0L))
  check(qEditar(0L, BigDecimal("0"), ""))
}
