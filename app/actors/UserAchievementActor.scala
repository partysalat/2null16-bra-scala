package actors

import akka.actor.{Actor, Props, Stash}
import models.{DrinkType, News, NewsStats}
import play.api.Logger
import repos.achievements.AchievementsRepository
import repos.drinks.DrinksRepository
import repos.news.NewsRepository

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

object UserAchievementActor {
  def props(userId: Int, newsRepository: NewsRepository, drinksRepository: DrinksRepository, achievementsRepository: AchievementsRepository)(implicit executionContext: ExecutionContext) = {
    Props(new UserAchievementActor(userId, newsRepository, drinksRepository, achievementsRepository))
  }

  case class ProcessDrinkNews(news: News)

}


class UserAchievementActor(userId: Int, newsRepository: NewsRepository, drinksRepository: DrinksRepository, achievementsRepository: AchievementsRepository)
                          (implicit ec: ExecutionContext) extends Actor with Stash {

  import UserAchievementActor._

  var achievementMetrics: AchievementMetrics = AchievementMetrics()

  override def preStart = {
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
    case ProcessDrinkNews(news) =>
      Logger.info(s"User $userId has drink with id ${news.drinkId}")
      if (isDrinkNews(news)) {
        increaseCounters(news).map(_ => achievementMetrics.checkAchievements)
      }
    case _ => Logger.info("huh?")
  }

  def increaseCounters(news: News): Future[Unit] = {
    import Property._
    drinksRepository.getById(news.drinkId.get).map(_.`type`).map {
      case DrinkType.BEER => achievementMetrics.addValue(beerProperties.keySet.toList, news.cardinality)
      case DrinkType.COCKTAIL => achievementMetrics.addValue(cocktailProperties.keySet.toList, news.cardinality)
      case DrinkType.SHOT => achievementMetrics.addValue(shotProperties.keySet.toList, news.cardinality)
      case DrinkType.SOFTDRINK => achievementMetrics.addValue(softdrinkProperties.keySet.toList, news.cardinality)
      case _ => ()
    }
  }

  private def isDrinkNews(news: News) = {
    news.drinkId match {
      case Some(drinkId) => true
      case None => false
    }

  }

  def initializeAchievementMetrics: Future[Unit] = {
    newsRepository
      .getStatsForUser(userId)
      .map(initAchievements)
  }

  private def initAchievements(stats: NewsStats) = {
    import Property._
    initCounter(beerProperties, stats.beerCount.getOrElse(0))
    initCounter(cocktailProperties, stats.cocktailCount.getOrElse(0))
    initCounter(shotProperties, stats.shotCount.getOrElse(0))
    initCounter(softdrinkProperties, stats.softdrinkCount.getOrElse(0))


    AchievementDefinitions.achievements
      .map(_.copy())
      .foreach(achievementMetrics.defineAchievement)
    Logger.info(achievementMetrics.checkAchievements.toString())

  }

  private def initCounter(properties: mutable.Map[String, Property], value: Int) = {
    properties.foreach {
      case (_, property) => achievementMetrics.defineProperty(property.copy(initialValue = value, value = value))
    }

  }

  case class InitializationDone()

}