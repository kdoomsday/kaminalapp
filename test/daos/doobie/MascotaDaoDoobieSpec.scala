package daos.doobie

import doobie.specs2.imports.AnalysisSpec
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import testutil.TestUtil

object MascotaDaoDoobieSpec extends Specification with AnalysisSpec {
  val transactor = TestUtil.transactor()

  check(MascotaDaoDoobie.qGuardarMascota("", Some(""), Some(0), Some(new DateTime()), 1L))
}
