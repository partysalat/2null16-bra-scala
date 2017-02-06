package news.repos

import achievements.repos.AchievementsTable
import common.BaseTable
import drinks.repos.DrinksTable
import news.models.NewsType.NewsType
import news.models.{News, NewsType}
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile
import users.repos.UsersTable


trait NewsTable extends BaseTable with DrinksTable with AchievementsTable with UsersTable{
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  lazy protected val news = TableQuery[NewsTable]
  lazy protected val newsInc = news returning news.map(_.id)

  implicit lazy val newsMapper = MappedColumnType.base[NewsType, String](
    e => e.toString,
    s => NewsType.withName(s)
  )


  private[NewsTable] class NewsTable(tag: Tag) extends Table[News](tag, "news") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val cardinality: Rep[Int] = column[Int]("cardinality", O.SqlType("VARCHAR(200)"))
    val newsType: Rep[NewsType] = column[NewsType]("type", O.SqlType("VARCHAR(200)"))
    val referenceId: Rep[Int] = column[Int]("referenceId", O.SqlType("VARCHAR(200)"))
    val userId: Rep[Int] = column[Int]("userId", O.SqlType("VARCHAR(200)"))
    val createdAt: Rep[DateTime] = column[DateTime]("createdAt", O.SqlType("date"))
    val updatedAt: Rep[DateTime] = column[DateTime]("updatedAt", O.SqlType("date"))


    def * = (cardinality, newsType, id.?, referenceId,userId.?,createdAt, updatedAt) <> ((News.apply _).tupled, News.unapply)
    val usersFK= foreignKey("userId",userId,users)(_.id)

  }

}