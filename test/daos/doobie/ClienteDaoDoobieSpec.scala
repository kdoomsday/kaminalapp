package daos.doobie

import doobie.specs2.imports.AnalysisSpec
import org.specs2.mutable.Specification
import testutil.TestUtil

object ClienteDaoDoobieSpec extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(ClienteDaoDoobie.qAddCliente(""))
  check(ClienteDaoDoobie.qClientes())
  check(ClienteDaoDoobie.qClientesSaldo())
}
