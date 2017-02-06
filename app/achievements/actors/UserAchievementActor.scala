package achievements.actors

import achievements.AchievementDefinitions
import achievements.models.Achievement
import achievements.repos.AchievementsRepository
import akka.actor.{Actor, Props, Stash}
import drinks.repos.DrinksRepository
import news.models.{News, NewsStats, NewsType}
import news.repos.NewsRepository
import play.api.Logger
import websocket.WebsocketService

import scala.concurrent.{ExecutionContext, Future}

object UserAchievementActor {
  def props(userId: Int, newsStats: NewsStats, statsForAll: NewsStats, newsRepository: NewsRepository, drinksRepository: DrinksRepository, achievementsRepository: AchievementsRepository, websocketService: WebsocketService)(implicit executionContext: ExecutionContext) = {
    Props(new UserAchievementActor(userId, newsStats,statsForAll, newsRepository, drinksRepository, achievementsRepository, websocketService))
  }

  case class ProcessDrinkNews(news: News)

}


class UserAchievementActor(userId: Int, newsStats: NewsStats,statsForAll:NewsStats, newsRepository: NewsRepository, drinksRepository: DrinksRepository, achievementsRepository: AchievementsRepository, websocketService: WebsocketService)
                          (implicit ec: ExecutionContext) extends Actor with Stash {

  import UserAchievementActor._

  import scala.collection.mutable

  var achievementMetrics: AchievementMetrics = AchievementMetrics()

  override def preStart = {
    Logger.debug(s"Started Actor with userId $userId")
    initializeAchievementMetrics.onSuccess({
      case _ => self ! InitializationDone
    })
  }

  def receive = initialReceive

  def initialReceive: Receive = {
    case InitializationDone => {
      context.become(normalReceive)
      unstashAll()
    }
    case _ => stash()
  }

  def normalReceive: Receive = {
    case ProcessDrinkNews(news) if news.userId.contains(userId) && news.`type` == NewsType.DRINK =>
      Logger.debug(s"actor $userId received news $news")
      Logger.info(s"User $userId has drink with id ${news.referenceId}")

      increaseCounters(news)
        .flatMap(_=> increaseAllCounters(news))
        .map(_ => achievementMetrics.checkAchievements)
        .flatMap(addAchievementsToUser)
    case ProcessDrinkNews(news) if news.`type` == NewsType.DRINK =>
      increaseAllCounters(news)
    case _ => ()
  }

  def addAchievementsToUser(unlockedAchievements: List[AchievementConstraints]) = {
    Logger.info(s"User $userId has unlocked achievements ${unlockedAchievements.toString()}")

    achievementsRepository
      .getByNames(unlockedAchievements.map(_.achievement.name))
      .flatMap {
        achievements: List[Achievement] => {
          val achievementNewsList = achievements.map(achievement => News(1, NewsType.ACHIEVEMENT, userId = Some(userId), referenceId = achievement.id.get))
          newsRepository.insertAll(achievementNewsList)
        }
      }.map(websocketService.notify)

  }
  def increaseAllCounters(news:News): Future[Unit] = {
    import AchievementCounterType._
    import AchievementDrinkType._
    import Property._
    achievementMetrics.addValues(countProperties(DRINK_COUNT)(ALL).keySet.toList, news.cardinality)
    drinksRepository.getById(news.referenceId)
      .map { drink =>
        achievementMetrics.addValues(countProperties(drink.`type`.toCounterType)(ALL).keySet.toList, news.cardinality,Some(drink.name))
        achievementMetrics.addValues(countProperties(CUSTOM)(ALL).keySet.toList, news.cardinality,Some(drink.name))
      }
  }

  def increaseCounters(news: News): Future[Unit] = {
    import AchievementCounterType._
    import AchievementDrinkType._
    import Property._
    achievementMetrics.addValues(countProperties(DRINK_COUNT)(USER).keySet.toList, news.cardinality)
    drinksRepository.getById(news.referenceId)
      .map { drink =>
        achievementMetrics.addValues(countProperties(drink.`type`.toCounterType)(USER).keySet.toList, news.cardinality,Some(drink.name))
        achievementMetrics.setValues(countProperties(drink.`type`.toCounterType)(AT_ONCE).keySet.toList, news.cardinality,Some(drink.name))

        achievementMetrics.addValues(countProperties(CUSTOM)(USER).keySet.toList, news.cardinality,Some(drink.name))
        achievementMetrics.setValues(countProperties(CUSTOM)(AT_ONCE).keySet.toList, news.cardinality,Some(drink.name))
      }
  }

  def initializeAchievementMetrics: Future[Unit] = {
    import AchievementCounterType._
    import AchievementDrinkType._
    import Property._
    AchievementDefinitions.achievements
      .map(_.copy())
      .foreach(achievementMetrics.defineAchievement)

    initCounter(countProperties(DRINK_COUNT)(USER), newsStats.drinkCount.getOrElse(0))
    initCounter(countProperties(BEER)(USER), newsStats.beerCount.getOrElse(0))
    initCounter(countProperties(COCKTAIL)(USER), newsStats.cocktailCount.getOrElse(0))
    initCounter(countProperties(SHOT)(USER), newsStats.shotCount.getOrElse(0))
    initCounter(countProperties(SOFTDRINK)(USER), newsStats.softdrinkCount.getOrElse(0))
    initCounter(countProperties(CUSTOM)(USER), 0)

    initCounter(countProperties(DRINK_COUNT)(ALL), statsForAll.drinkCount.getOrElse(0))
    initCounter(countProperties(BEER)(ALL), statsForAll.beerCount.getOrElse(0))
    initCounter(countProperties(COCKTAIL)(ALL), statsForAll.cocktailCount.getOrElse(0))
    initCounter(countProperties(SHOT)(ALL), statsForAll.shotCount.getOrElse(0))
    initCounter(countProperties(SOFTDRINK)(ALL), statsForAll.softdrinkCount.getOrElse(0))
    initCounter(countProperties(CUSTOM)(AT_ONCE), 0)

    AchievementDrinkType.values.foreach(drinkType=>initCounter(countProperties(drinkType)(AT_ONCE), 0))


    newsRepository.getAchievementsForUser(userId)
      .map(achievementMetrics.unlockReachedAchievements)
  }

  private def initCounter(properties: mutable.Map[String, Property], value: Int) = {
    properties.foreach {
      case (_, property) => achievementMetrics.defineProperty(property.copy(initialValue = value, value = value))
    }

  }

  case class InitializationDone()

}