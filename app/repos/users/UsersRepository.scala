package repos.users


import com.google.inject.{Inject, Singleton}
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class UsersRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UsersTable with HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._
  def insert(user: User): Future[Int] = db.run {
    userInc += user
  }

  def getAll(): Future[List[User]] = db.run {
    users.to[List].result
  }

  def insertAll(users: List[User]) = db.run {
    userInc ++= users
  }

  def update(user: User): Future[Int] = db.run {
    users.filter(_.id === user.id).update(user)
  }

  def delete(id: Int): Future[Int] = db.run {
    users.filter(_.id === id).delete
  }


  def getById(empId: Int): Future[Option[User]] = db.run {
    users.filter(_.id === empId).result.headOption
  }

  def ddl = users.schema
}


