package repos.news

import com.google.inject.{Inject, Singleton}
import models.News
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repos.drinks.DrinksTable
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class NewsRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends NewsTable with DrinksTable with HasDatabaseConfigProvider[JdbcProfile] {

  def insert(item: News): Future[Int] = db.run {
    newsInc += item
  }
  def insertAll(items: List[News]) = db.run {
    newsInc ++= items
  }


}


