package models

import models.NewsType.NewsType
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._


object NewsType extends Enumeration {
  type NewsType = Value
  val DRINK = Value("DRINK")
  val ACHIEVEMENT = Value("ACHIEVEMENT")

  implicit val newsTypeFormat = new Format[NewsType] {
    def reads(json: JsValue) = JsSuccess(NewsType.withName(json.as[String]))
    def writes(enum: NewsType) = JsString(enum.toString)
  }
  //implicit val newsTypeFormat: Format[NewsType] = Json.format[NewsType]
}

case class News(
                 cardinality: Int,
                 `type`: NewsType,
                 id: Option[Int] = None,
                 achievementId: Option[Int] = None,
                 drinkId: Option[Int] = None,
                 userId: Option[Int] = None,
                 createdAt: DateTime = DateTime.now(),
                 updatedAt: DateTime = DateTime.now()

               )
object News {
  implicit val newsFormat: Format[News] = Json.format[News]
}



case class CreateDrinkNewsDto(drink: Int, users: List[UserNews])
object CreateDrinkNewsDto {
  implicit val createDrinkNewsDtoFormat: Format[CreateDrinkNewsDto] = Json.format[CreateDrinkNewsDto]
}

case class UserNews(id: Int, cardinality: Int)
object UserNews {
  implicit val userNewsFormat: Format[UserNews] = Json.format[UserNews]
}
case class NewsWithItems(news:News,user:Option[User], drink:Option[Drink]=None, achievement:Option[Achievement]= None)
object NewsWithItems {
  implicit val newsWithItemsFormat: Reads[NewsWithItems] = Json.reads[NewsWithItems]
  implicit val newsWithItemsWrites: Writes[NewsWithItems] = (
    JsPath.write[News] and
    (JsPath \ "user").write[Option[User]] and
    (JsPath \ "drink").write[Option[Drink]] and
    (JsPath \ "achievement").write[Option[Achievement]]
  )(unlift(NewsWithItems.unapply))
}

case class NewsResponse(news:List[NewsWithItems])
object NewsResponse {
  implicit val newsResponseFormat: Format[NewsResponse] = Json.format[NewsResponse]
}
