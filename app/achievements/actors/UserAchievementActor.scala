package achievements.actors

import achievements.AchievementDefinitions
import achievements.repos.AchievementsRepository
import akka.actor.{Actor, Props, Stash}
import models.{DrinkType, News, NewsStats, NewsType}
import play.api.Logger
import repos.drinks.DrinksRepository
import repos.news.NewsRepository

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

object UserAchievementActor {
  def props(userId: Int, newsStats: NewsStats, newsRepository: NewsRepository, drinksRepository: DrinksRepository, achievementsRepository: AchievementsRepository)(implicit executionContext: ExecutionContext) = {
    Props(new UserAchievementActor(userId, newsStats, newsRepository, drinksRepository, achievementsRepository))
  }

  case class ProcessDrinkNews(news: News)

}


class UserAchievementActor(userId: Int, newsStats: NewsStats, newsRepository: NewsRepository, drinksRepository: DrinksRepository, achievementsRepository: AchievementsRepository)
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
      if (isDrinkNews(news)) {
        Logger.info(s"User $userId has drink with id ${news.drinkId}")
        increaseCounters(news)
          .map(_ => achievementMetrics.checkAchievements)
          .map(addAchievementsToUser)
      }
    case _ => Logger.info("huh?")
  }

  def addAchievementsToUser(unlockedAchievements: List[AchievementConstraints]) = {
    Logger.info(s"User $userId has unlocked achievements ${unlockedAchievements.toString()}")
    unlockedAchievements.map { ac =>
      achievementsRepository.getByName(ac.achievement.name).flatMap { achievement =>
        newsRepository.insert(News(1, NewsType.ACHIEVEMENT, userId = Some(userId), achievementId = achievement.id))
      }

    }
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
      case Some(_) => true
      case None => false
    }

  }

  def initializeAchievementMetrics: Future[Unit] = {
    import Property._
    initCounter(beerProperties, newsStats.beerCount.getOrElse(0))
    initCounter(cocktailProperties, newsStats.cocktailCount.getOrElse(0))
    initCounter(shotProperties, newsStats.shotCount.getOrElse(0))
    initCounter(softdrinkProperties, newsStats.softdrinkCount.getOrElse(0))


    AchievementDefinitions.achievements
      .map(_.copy())
      .foreach(achievementMetrics.defineAchievement)
    val previousAchievements = achievementMetrics.checkAchievements
    Logger.debug(s"Initial unlocked achievements: ${previousAchievements.toString()}")
    Future.successful(():Unit)
  }

  private def initCounter(properties: mutable.Map[String, Property], value: Int) = {
    properties.foreach {
      case (_, property) => achievementMetrics.defineProperty(property.copy(initialValue = value, value = value))
    }

  }

  case class InitializationDone()

}