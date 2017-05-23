package audits

import doobie.imports._
import javax.inject.Inject
import play.api.Logger
import play.api.db.Database

/** Implementaci&oacute;n con doobie de dao de eventos */
class EventDaoDoobie @Inject() (db: Database) extends EventDao {
  import daos.doobie.DoobieTransactor.transactor
  import EventDaoDoobie.qWrite

  def write(description: String): Unit = {
    Logger.debug(description)
    qWrite(description).run.transact(transactor(db))
  }
}

object EventDaoDoobie {
  def qWrite(description: String): Update0 =
    sql"""insert into events(description, moment)
          values($description, now())""".update
}
