package controllers

import java.util.NoSuchElementException
import javax.inject._

import akka.actor.ActorSystem
import drinks.models.{CreateDrinkDto, Drink, DrinkResponse, DrinkType}
import drinks.repos.DrinksRepository
import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import users.models.CreatedResponse

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class DrinkController @Inject()(actorSystem: ActorSystem, drinksRepository: DrinksRepository)
                               (implicit exec: ExecutionContext) extends Controller {
  val logger: Logger = Logger(this.getClass)

  def getDrinks(drinkTypeString: String) = Action.async {
    Future(DrinkType.withName(drinkTypeString.toUpperCase))
      .flatMap(drinksRepository.getAll)
      .map(drinks => Ok(Json.toJson(DrinkResponse(drinks))))
      .recoverWith({
        case e: NoSuchElementException => Future {
          BadRequest(e.toString)
        }
        case e => Future {
          logger.error(e.toString)
          InternalServerError
        }
      })
  }

  def saveDrink(drinkTypeString: String) = Action.async(parse.json[CreateDrinkDto]) { request =>
    val drinkName = request.body.name
    Future(DrinkType.withName(drinkTypeString.toUpperCase))
      .map(Drink(drinkName, _))
      .flatMap(drinksRepository.insert)
      .map(id => Ok(Json.toJson(CreatedResponse(id))))
      .recoverWith({
        case e: NoSuchElementException => Future {
          BadRequest(e.toString)
        }
        case e => Future {
          logger.error(e.toString)
          InternalServerError
        }
      })
  }

}





