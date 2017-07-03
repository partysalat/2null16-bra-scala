package news.repos

import achievements.models.Achievement
import achievements.repos.AchievementsTable
import camera.repos.NewsImagesTable
import com.google.inject.{Inject, Singleton}
import drinks.models.Drink
import drinks.models.DrinkType.DrinkType
import drinks.repos.DrinksTable
import news.models.{News, NewsStats, NewsType, NewsWithItems}
import org.joda.time.{DateTime, LocalDateTime}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import users.models.User
import users.repos.UsersTable

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class NewsRepository @Inject()(
    override protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext)
    extends NewsTable
    with UsersTable
    with DrinksTable
    with AchievementsTable
    with NewsImagesTable
    with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val PAGE_SIZE = 20

  def insert(item: News): Future[Int] = db.run {
    newsInc += item
  }

  def insertAll(items: List[News]) = db.run {
    newsInc ++= items
  }

  def getAll(skip: Int, limit: Int = PAGE_SIZE): Future[List[NewsWithItems]] =
    db.run {
      val joinQuery = for {
        ((((newsItem, user), drink), achievement), newsImages) <- news joinLeft users on (_.userId === _.id) joinLeft drinks on (_._1.referenceId === _.id) joinLeft achievements on (_._1._1.referenceId === _.id) joinLeft newsImages on (_._1._1._1.referenceId === _.id)
      } yield (newsItem, user, drink, achievement, newsImages)

      joinQuery
        .sortBy(_._1.createdAt.desc)
        .drop(skip)
        .take(limit)
        .to[List]
        .result
        .map((news) => {
          news.map(item =>
            NewsWithItems(item._1, item._2, item._3, item._4, item._5))
        })
    }

  implicit val getStatsResult: GetResult[NewsStats] = GetResult(
    r => NewsStats(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, User(r.<<, r.<<)))

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
     LEFT OUTER JOIN `drinks` AS `drink` ON `news`.`referenceId` = `drink`.`id`
    """

  def getStats: Future[List[NewsStats]] = {

    val action = sql"""
           #$selectStats
           WHERE `news`.`type` = "DRINK" GROUP BY `userId` ORDER BY `drinkCount` DESC;
      """.as[NewsStats]
    db.run(action).map(l => l.to[List])
  }
  def getStatsForAll: Future[NewsStats] = {
    val action = sql"""
           #$selectStats
           WHERE `news`.`type` = "DRINK";
      """.as[NewsStats]
    db.run(action.head)
  }

  def getStatsForUser(userId: Int): Future[NewsStats] = {
    val action = sql"""
           #$selectStats
           WHERE `news`.`type` = "DRINK" AND `userId`=${userId.toString};
      """.as[NewsStats]
    db.run(action.head)

  }

  def getAchievements
    : Future[List[(News, Option[User], Option[Achievement])]] = db.run {
    val achievementNews = news.filter(_.`newsType` === NewsType.ACHIEVEMENT)
    val joinQuery = for {
      ((newsItem, user), achievements) <- achievementNews joinLeft users on (_.userId === _.id) joinLeft achievements on (_._1.referenceId === _.id)
    } yield (newsItem, user, achievements)

    joinQuery
      .to[List]
      .result
      .map((news: List[(News, Option[User], Option[Achievement])]) => {
        news.map(item => item)
      })
  }
  def getAchievementsForUser(userId: Int): Future[List[Achievement]] = db.run {
    val joinQuery = for {
      (_, achievement) <- news
        .filter(_.`newsType` === NewsType.ACHIEVEMENT)
        .filter(_.userId === userId) joinLeft achievements on (_.referenceId === _.id)
    } yield achievement

    joinQuery
      .to[List]
      .result
      .map((achievements: List[Option[Achievement]]) => {
        achievements.map(_.get)
      })
  }

  def getNewsByIds(ids: Seq[Int]): Future[List[NewsWithItems]] = db.run {
    val joinQuery = for {
      ((((newsItem, user), drink), achievement), image) <- news.filter(
        _.id inSetBind ids) joinLeft users on (_.userId === _.id) joinLeft drinks on (_._1.referenceId === _.id) joinLeft achievements on (_._1._1.referenceId === _.id) joinLeft newsImages on (_._1._1._1.referenceId === _.id)
    } yield (newsItem, user, drink, achievement, image)

    joinQuery
      .sortBy(_._1.createdAt.desc)
      .to[List]
      .result
      .map((news) => {
        news.map(item =>
          NewsWithItems(item._1, item._2, item._3, item._4, item._5))
      })
  }

  def removeNews(newsId: Int) = db.run {
    news.filter(_.id === newsId).delete
  }

  def emptyTable = db.run {
    news.delete
  }
  def getLatestUserWithType(drinkType: DrinkType, date: DateTime):Future[Seq[Option[User]]] =
    db.run {
      val drinkQuery = news.filter(_.newsType === NewsType.DRINK).filter(_.createdAt <= date)
      val joinQuery = for {
        ((newsItem, user), drink) <- drinkQuery joinLeft users on (_.userId === _.id) joinLeft drinks on ((tuple,drinks)=>tuple._1.referenceId === drinks.id && drinks.drinkType === drinkType)
      } yield (newsItem, user, drink)

      joinQuery
      .sortBy(_._1.createdAt.desc)
      .result
      .map {
        resultlist =>
          resultlist
            .groupBy(_._1.createdAt)
            .toList
            .sortBy(_._1.getMillis())
            .last._2

            .map(_._2)
      }
//    val drinkQuery = news.filter(_.newsType === NewsType.DRINK)
//    val joinQuery = for {
//      ((newsItem, user), drink) <- drinkQuery joinLeft users on (_.userId === _.id) joinLeft drinks on (_._1.referenceId === _.id)
//    } yield (newsItem, user, drink)
//    joinQuery
//      .filter {
//        tuple => tuple._3.`type` === drinkType
//      }
//      .filter {
//        tuple => tuple._1.date < date
//      }
//      .sortBy(_._1.date.asc)
    }

  def getDrinkNews = db.run {
    val drinkQuery = news.filter(_.newsType === NewsType.DRINK)
    val joinQuery = for {
      ((newsItem, user), drink) <- drinkQuery joinLeft users on (_.userId === _.id) joinLeft drinks on (_._1.referenceId === _.id)
    } yield (newsItem, user, drink)
    joinQuery
      .sortBy(_._1.createdAt.desc)
      .to[List]
      .result
  }
  def hasAchievementReached(achievement:Achievement, userId:Int) = db.run {
    val query = for {
      achievementDbEntity <- achievements.filter(_.achievementName === achievement.name)
      newsDbEntity <- news
        .filter(_.newsType === NewsType.ACHIEVEMENT)
        .filter(_.userId === userId)
        .filter(_.referenceId === achievementDbEntity.id)
    } yield newsDbEntity
    query.exists.result
  }
}
