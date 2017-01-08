package models

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}


case class Achievement(
                        name: String,
                        description: String,
                        imagePath: String,
                        id: Option[Int] = None,
                        createdAt: DateTime = DateTime.now(),
                        updatedAt: DateTime = DateTime.now()
                      )
object Achievement {
  implicit val achievementFormat: Format[Achievement] = Json.format[Achievement]
}

