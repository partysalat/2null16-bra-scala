package camera.models

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

case class NewsImage(
                  path: String,
                  id: Option[Int] = None,
                  createdAt: DateTime = DateTime.now(),
                  updatedAt: DateTime = DateTime.now()
                )

object NewsImage {
  implicit val newsImageFormat: Format[NewsImage] = Json.format[NewsImage]
}