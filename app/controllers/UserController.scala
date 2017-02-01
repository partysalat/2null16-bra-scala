package controllers

import javax.inject._

import akka.actor.ActorSystem
import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import repos.users.UsersRepository

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserController @Inject()(userRepository: UsersRepository)
                              (implicit exec: ExecutionContext) extends Controller {
  val logger: Logger = Logger(this.getClass)

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

  def createUser = Action.async(parse.json[CreateUserDto]) { request =>
    val user = User(request.body.name)
    userRepository
      .insert(user)
      .map(id => Ok(Json.toJson(CreatedResponse(id))))
      .recoverWith({
        case e => Future {
          logger.error(e.toString)
          InternalServerError
        }
      })
  }

}





