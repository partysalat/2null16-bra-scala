package controllers

import javax.inject._

import akka.actor.ActorSystem
import models._
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import repos.users.UsersRepository

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserController @Inject()(actorSystem: ActorSystem, userRepository: UsersRepository)
                              (implicit exec: ExecutionContext) extends Controller {
  val logger:Logger = Logger(this.getClass)

  def getUsers = Action.async {
    userRepository.getAll()
      .map(users => Ok(Json.toJson(UsersResponse(users))))
      .recoverWith({
        case e => Future {
          logger.error(e.toString)
          InternalServerError
        }
      })
  }

  def createUser = Action.async(parse.tolerantJson) { request =>
    val validationResult = request.body.validate[CreateUserDto]

    validationResult match {
      case e: JsError => Future {
        BadRequest(e.toString)
      }
      case userJson: JsSuccess[CreateUserDto] => {
        userRepository
          .insert(User(userJson.get.name))
          .map(userId => Ok(Json.toJson(CreatedResponse(userId))))
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



