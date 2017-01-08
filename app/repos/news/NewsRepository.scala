package repos.news

import com.google.inject.{Inject, Singleton}
import models.{Drink, News, NewsWithItems, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repos.drinks.DrinksTable
import repos.users.UsersTable
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class NewsRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends NewsTable with UsersTable with DrinksTable with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def insert(item: News): Future[Int] = db.run {
    newsInc += item
  }

  def insertAll(items: List[News]) = db.run {
    newsInc ++= items
  }

  def getAll(): Future[List[News]] = db.run {
    news.to[List].result


  }

  def getAllWithJoins(): Future[List[(News, User, Drink)]] = db.run {

    val zipWithJoin = for {
      newsItem <- news
      user <- users if newsItem.userId === user.id
      drink <- drinks if newsItem.drinkId === drink.id
    } yield (newsItem, user,drink )
    zipWithJoin.to[List].result

  }

}


