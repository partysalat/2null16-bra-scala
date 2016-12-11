package models

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

case class CreateUserDto(name: String)
object CreateUserDto {
  implicit val createUserDtoJsonFormat: Format[CreateUserDto] = Json.format[CreateUserDto]
}

case class User(name: String, id: Option[Int] = None, createdAt: DateTime=DateTime.now(), updatedAt:DateTime=DateTime.now())
object User {
  implicit val userJsonFormat: Format[User] = Json.format[User]
}

case class UsersResponse(users:List[User])
object UsersResponse{
  implicit val userResponseJsonFormat: Format[UsersResponse] = Json.format[UsersResponse]
}


case class CreatedResponse(id:Int)
object CreatedResponse{
  implicit val userCreatedResponseJsonFormat: Format[CreatedResponse] = Json.format[CreatedResponse]
}