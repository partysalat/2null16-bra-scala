package news.models

import achievements.models.Achievement
import camera.models.NewsImage
import drinks.models.Drink
import news.models.NewsType.NewsType
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import users.models.User


object NewsType extends Enumeration {
  type NewsType = Value
  val DRINK = Value("DRINK")
  val ACHIEVEMENT = Value("ACHIEVEMENT")
  val IMAGE = Value("IMAGE")

  implicit val newsTypeFormat = new Format[NewsType] {
    def reads(json: JsValue) = JsSuccess(NewsType.withName(json.as[String]))
    def writes(enum: NewsType) = JsString(enum.toString)
  }
}

case class News(
                 cardinality: Int,
                 `type`: NewsType,
                 id: Option[Int] = None,
                 referenceId: Int,
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
case class NewsWithItems(news:News,user:Option[User], drink:Option[Drink]=None, achievement:Option[Achievement]= None, image:Option[NewsImage]=None)
object NewsWithItems {
  implicit val newsWithItemsFormat: Reads[NewsWithItems] = Json.reads[NewsWithItems]
  implicit val newsWithItemsWrites: Writes[NewsWithItems] = Writes[NewsWithItems] { item =>

  Json.obj(
    "userId"->item.news.userId,
    "type"->item.news.`type`,
    "cardinality"->item.news.cardinality,
    "referenceId"->item.news.referenceId,
    "createdAt"->item.news.createdAt,
    "updatedAt"->item.news.updatedAt,
    "id"->item.news.id,
    "user"->item.user,
    "achievement"->item.achievement,
    "drink"->item.drink,
    "image"->item.image
  )

  }
}

case class NewsResponse(news:List[NewsWithItems])
object NewsResponse {
  implicit val newsResponseFormat: Format[NewsResponse] = Json.format[NewsResponse]
}

case class NewsStats(
  drinkCount:Option[Int],
  beerCount:Option[Int],
  cocktailCount:Option[Int],
  shotCount:Option[Int],
  coffeeCount:Option[Int],
  softdrinkCount:Option[Int],
  user:User
)
object NewsStats {
  implicit val newsStatsFormat: Format[NewsStats] = Json.format[NewsStats]
}
case class NewsStatsResponse(bestlist:List[NewsStats])
object NewsStatsResponse {
  implicit val newsStatsResponseFormat: Format[NewsStatsResponse] = Json.format[NewsStatsResponse]
}
