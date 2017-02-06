package camera.repos

import camera.models.NewsImage
import common.BaseTable
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

trait NewsImagesTable extends BaseTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  lazy protected val newsImages = TableQuery[NewsImagesTable]
  lazy protected val newsImagesInc = newsImages returning newsImages.map(_.id)



  private[NewsImagesTable] class NewsImagesTable(tag: Tag) extends Table[NewsImage](tag, "images") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val path: Rep[String] = column[String]("name", O.SqlType("VARCHAR(200)"))
    val createdAt: Rep[DateTime] = column[DateTime]("createdAt", O.SqlType("date"))
    val updatedAt: Rep[DateTime] = column[DateTime]("updatedAt", O.SqlType("date"))


    def * = (path, id.?, createdAt, updatedAt) <> ((NewsImage.apply _).tupled, NewsImage.unapply)
  }

}