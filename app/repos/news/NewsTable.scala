package repos.news

import models.NewsType.NewsType
import models.{Drink, News, NewsType}
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import repos.BaseTable
import slick.driver.JdbcProfile


private[repos] trait NewsTable extends BaseTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  lazy protected val tableQuery = TableQuery[NewsTable]
  lazy protected val tableQueryInc = tableQuery returning tableQuery.map(_.id)

  implicit lazy val newsMapper = MappedColumnType.base[NewsType, String](
    e => e.toString,
    s => NewsType.withName(s)
  )


  private[NewsTable] class NewsTable(tag: Tag) extends Table[News](tag, "news") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val cardinality: Rep[Int] = column[Int]("cardinality", O.SqlType("VARCHAR(200)"))
    val newsType: Rep[NewsType] = column[NewsType]("type", O.SqlType("VARCHAR(200)"))
    val createdAt: Rep[DateTime] = column[DateTime]("createdAt", O.SqlType("date"))
    val updatedAt: Rep[DateTime] = column[DateTime]("updatedAt", O.SqlType("date"))


    def * = (cardinality, newsType, id.?, createdAt, updatedAt) <> ((News.apply _).tupled, News.unapply)
  }

}