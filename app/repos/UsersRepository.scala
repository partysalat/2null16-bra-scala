package repos

import java.sql.Date
import javax.inject.Inject

import com.google.inject.Singleton
import models.User
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class UsersRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UsersTable with HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._
  def insert(user: User): Future[Int] = db.run {
    userTableQueryInc += user
  }

  def getAll(): Future[List[User]] = db.run {
    userTableQuery.to[List].result
  }

  def insertAll(users: List[User]) = db.run {
    userTableQueryInc ++= users
  }
}

trait BaseTable{
  self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  implicit def dateTime =
    MappedColumnType.base[DateTime, Date](
      dateTime => new Date(dateTime.getMillis),
      date => new DateTime(date.getTime)
    )
}

private[repos] trait UsersTable extends BaseTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  lazy protected val userTableQuery = TableQuery[UsersTable]
  lazy protected val userTableQueryInc = userTableQuery returning userTableQuery.map(_.id)

  private[UsersTable] class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val userName: Rep[String] = column[String]("userName", O.SqlType("VARCHAR(200)"))
    val createdAt: Rep[DateTime] = column[DateTime]("createdAt", O.SqlType("date"))


    def * = (userName, createdAt, id.?) <> ((User.apply _).tupled, User.unapply)
  }

}