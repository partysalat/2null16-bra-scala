package drinks.models

import drinks.models.DrinkType.DrinkType
import org.joda.time.DateTime
import play.api.libs.json._

object DrinkType extends Enumeration {
  type DrinkType = Value
  val COCKTAIL = Value("COCKTAIL")
  val SHOT = Value("SHOT")
  val BEER = Value("BEER")
  val COFFEE = Value("COFFEE")
  val SOFTDRINK = Value("SOFTDRINK")
  implicit val newsTypeFormat = new Format[DrinkType] {
    def reads(json: JsValue) = JsSuccess(DrinkType.withName(json.as[String]))
    def writes(enum: DrinkType) = JsString(enum.toString)
  }

}

case class Drink(
                  name: String,
                  `type`: DrinkType,
                  id: Option[Int] = None,
                  createdAt: DateTime = DateTime.now(),
                  updatedAt: DateTime = DateTime.now()
                )

object Drink {
  implicit val drinkFormat: Format[Drink] = Json.format[Drink]
}
case class DrinkResponse(drinks:List[Drink])
object DrinkResponse{
  implicit val drinkResponseJsonFormat: Format[DrinkResponse] = Json.format[DrinkResponse]
}
case class CreateDrinkDto(name:String)
object CreateDrinkDto{
  implicit val createDrinkJsonFormat: Format[CreateDrinkDto] = Json.format[CreateDrinkDto]
}
