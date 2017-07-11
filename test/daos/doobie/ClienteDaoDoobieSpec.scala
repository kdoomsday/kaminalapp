package daos.doobie

import doobie.specs2.imports.AnalysisSpec
import org.specs2.mutable.Specification
import testutil.TestUtil

object ClienteDaoDoobieSpec extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(ClienteDaoDoobie.qAddCliente("", "", None, None, None))
  check(ClienteDaoDoobie.qClientes())
  check(ClienteDaoDoobie.qClientesSaldo())
  check(ClienteDaoDoobie.qById(0L))
  check(ClienteDaoDoobie.qAddTelefono("", 0L))
  check(ClienteDaoDoobie.qByMascota(0L))
  check(ClienteDaoDoobie.qUpdateNombre(0L, "", ""))
}
