package repos.news

import com.google.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repos.achievements.AchievementsTable
import repos.drinks.DrinksTable
import repos.users.UsersTable
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class NewsRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec:ExecutionContext) extends NewsTable with UsersTable with DrinksTable with AchievementsTable with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  val PAGE_SIZE = 10
  def insert(item: News): Future[Int] = db.run {
    newsInc += item
  }

  def insertAll(items: List[News]) = db.run {
    newsInc ++= items
  }

  def getAll(skip:Int): Future[List[NewsWithItems]] = db.run {
    val joinQuery = for {
      (((newsItem, user),drink),achievement) <- news joinLeft users on (_.userId === _.id) joinLeft drinks on (_._1.drinkId === _.id)  joinLeft achievements on (_._1._1.achievementId === _.id)
    } yield (newsItem, user, drink, achievement)

    joinQuery
      .sortBy(_._1.createdAt.desc)
      .drop(skip)
      .take(PAGE_SIZE).
      to[List].result
      .map((news: List[(News, Option[User], Option[Drink], Option[Achievement])]) => {
        news.map(item => NewsWithItems(item._1, item._2,item._3, item._4))
      })
  }

}


