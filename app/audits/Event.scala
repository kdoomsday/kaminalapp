package audits

import java.sql.Timestamp

/** Un evento o auditoria en el sistema */
case class Event(
  id: Long,
  description: String,
  moment: Timestamp
)
