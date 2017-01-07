package models

import models.NewsType.NewsType
import org.joda.time.DateTime

object NewsType extends Enumeration {
  type NewsType = Value
  val DRINK = Value("DRINK")
  val ACHIEVEMENT = Value("ACHIEVEMENT")
}

case class News(
                 cardinality: Int,
                 `type`: NewsType,
                 id: Option[Int] = None,
                 createdAt: DateTime = DateTime.now(),
                 updatedAt: DateTime = DateTime.now()
               )

