package controllers

import javax.inject._

import akka.actor.ActorSystem
import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import repos.drinks.DrinksRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}


@Singleton
class DrinkController @Inject()(actorSystem: ActorSystem, drinksRepository: DrinksRepository)
                               (implicit exec: ExecutionContext) extends Controller {
  val logger: Logger = Logger(this.getClass)

  def getDrinks (drinkTypeString:String)= Action.async {
    Try(DrinkType.withName(drinkTypeString.toUpperCase)) match {
      case Failure(e) => Future{ BadRequest(e.toString) }
      case Success(drinkType) => {
        drinksRepository.getAll(drinkType)
          .map(drinks => Ok(Json.toJson(DrinkResponse(drinks))))
          .recoverWith({
            case e => Future {
              logger.error(e.toString)
              InternalServerError
            }
          })
      }
    }
  }


}





