package repos.news

import com.google.inject.{Inject, Singleton}
import models.News
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class NewsRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends NewsTable with HasDatabaseConfigProvider[JdbcProfile] {

  def insert(item: News): Future[Int] = db.run {
    tableQueryInc += item
  }
  def insertAll(items: List[News]) = db.run {
    tableQueryInc ++= items
  }


}


