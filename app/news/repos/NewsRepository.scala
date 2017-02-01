package news.repos

import achievements.models.Achievement
import achievements.repos.AchievementsTable
import com.google.inject.{Inject, Singleton}
import drinks.models.Drink
import drinks.repos.DrinksTable
import news.models.{News, NewsStats, NewsType, NewsWithItems}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import users.models.User
import users.repos.UsersTable

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class NewsRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends NewsTable with UsersTable with DrinksTable with AchievementsTable with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val PAGE_SIZE = 10

  def insert(item: News): Future[Int] = db.run {
    newsInc += item
  }

  def insertAll(items: List[News]) = db.run {
    newsInc ++= items
  }

  def getAll(skip: Int, limit:Int = PAGE_SIZE): Future[List[NewsWithItems]] = db.run {
    val joinQuery = for {
      (((newsItem, user), drink), achievement) <- news joinLeft users on (_.userId === _.id) joinLeft drinks on (_._1.drinkId === _.id) joinLeft achievements on (_._1._1.achievementId === _.id)
    } yield (newsItem, user, drink, achievement)

    joinQuery
      .sortBy(_._1.createdAt.desc)
      .drop(skip)
      .take(limit)
      .to[List].result
      .map((news: List[(News, Option[User], Option[Drink], Option[Achievement])]) => {
        news.map(item => NewsWithItems(item._1, item._2, item._3, item._4))
      })
  }

  implicit val getStatsResult: GetResult[NewsStats] = GetResult(r => NewsStats(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, User(r.<<, r.<<)))

  val selectStats =
    """
     SELECT
       sum(`cardinality`) AS `drinkCount`,
       sum(CASE WHEN drink.type="BEER" THEN cardinality END) AS `beerCount`,
       sum(CASE WHEN drink.type="COCKTAIL" THEN cardinality END) AS `cocktailCount`,
       sum(CASE WHEN drink.type="SHOT" THEN cardinality END) AS `shotCount`,
       sum(CASE WHEN drink.type="COFFEE" THEN cardinality END) AS `coffeeCount`,
       sum(CASE WHEN drink.type="SOFTDRINK" THEN cardinality END) AS `softdrinkCount`,
       `user`.`name` AS `user.name`,
       `user`.`id` AS `user.id`
     FROM news AS `news`
     LEFT OUTER JOIN `users` AS `user` ON `news`.`userId` = `user`.`id`
     LEFT OUTER JOIN `drinks` AS `drink` ON `news`.`drinkId` = `drink`.`id`
    """

  def getStats: Future[List[NewsStats]] = {

    val action = sql"""
           #$selectStats
           WHERE `news`.`type` = "DRINK" GROUP BY `userId` ORDER BY `drinkCount` DESC;
      """.as[NewsStats]
    db.run(action).map(l => l.to[List])
  }

  def getStatsForUser(userId:Int): Future[NewsStats] = {
    val action = sql"""
           #$selectStats
           WHERE `news`.`type` = "DRINK" AND `userId`=${userId.toString};
      """.as[NewsStats]
    db.run(action.head)

  }

  def getAchievements: Future[List[(News, Option[User], Option[Achievement])]] = db.run {
    val achievementNews = news.filter(_.`newsType` === NewsType.ACHIEVEMENT)
    val joinQuery = for {
      ((newsItem, user), achievements) <- achievementNews joinLeft users on (_.userId === _.id) joinLeft achievements on (_._1.achievementId === _.id)
    } yield (newsItem, user, achievements)

    joinQuery
      .to[List]
      .result
      .map((news: List[(News, Option[User], Option[Achievement])]) => {
        news.map(item => item)
      })
  }

  def getNewsByIds(ids:Seq[Int]): Future[List[NewsWithItems]] =db.run{
    val joinQuery = for {
      (((newsItem, user), drink), achievement) <- news.filter(_.id inSetBind ids) joinLeft users on (_.userId === _.id) joinLeft drinks on (_._1.drinkId === _.id) joinLeft achievements on (_._1._1.achievementId === _.id)
    } yield (newsItem, user, drink, achievement)

    joinQuery
      .sortBy(_._1.createdAt.desc)
      .to[List].result
      .map((news: List[(News, Option[User], Option[Drink], Option[Achievement])]) => {
        news.map(item => NewsWithItems(item._1, item._2, item._3, item._4))
      })
  }

  def removeNews(newsId:Int) = db.run {
    news.filter(_.id === newsId).delete
  }
}


