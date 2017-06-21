package daos.doobie

import doobie.imports.Meta
import java.sql.Timestamp
import org.joda.time.{ DateTime, Instant }

/** Imports para facilitar el trabajo con Doobie */
object DoobieImports {
  implicit val InstantMeta: Meta[Instant] = Meta[Timestamp].nxmap(
    (t: Timestamp) ⇒ new Instant(t.getTime),
    (i: Instant) ⇒ new Timestamp(i.getMillis)
  )

  implicit val DateTimeMeta: Meta[DateTime] = Meta[Timestamp].nxmap(
    (t: Timestamp) ⇒ new DateTime(t.getTime),
    (d: DateTime) ⇒ new Timestamp(d.getMillis)
  )
}
