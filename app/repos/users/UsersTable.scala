package repos.users

import models.User
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import repos.BaseTable
import slick.driver.JdbcProfile

private[repos] trait UsersTable extends BaseTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  lazy protected val users = TableQuery[UsersTable]
  lazy protected val userInc = users returning users.map(_.id)

  private[UsersTable] class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val userName: Rep[String] = column[String]("name", O.SqlType("VARCHAR(200)"))
    val createdAt: Rep[DateTime] = column[DateTime]("createdAt", O.SqlType("DATETIME"))
    val updatedAt: Rep[DateTime] = column[DateTime]("updatedAt", O.SqlType("DATETIME"))

    def * = (userName, id.?, createdAt, updatedAt) <> ((User.apply _).tupled, User.unapply)
  }

}