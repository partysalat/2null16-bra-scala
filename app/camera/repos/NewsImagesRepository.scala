package camera.repos

import camera.models.NewsImage
import com.google.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class NewsImagesRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends NewsImagesTable with HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._
  def insert(item: NewsImage): Future[Int] = db.run {
    newsImages += item
  }
  def insertAll(items: List[NewsImage]) = db.run {
    newsImagesInc ++= items
  }
}


