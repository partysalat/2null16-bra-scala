package actors

import akka.actor.{Actor, Props, Stash}
import models.{News, NewsStats}
import play.api.Logger
import repos.drinks.DrinksRepository
import repos.news.NewsRepository

import scala.concurrent.ExecutionContext

object UserAchievementActor {
  def props(userId: Int, newsRepository: NewsRepository, drinksRepository: DrinksRepository)(implicit executionContext: ExecutionContext) = {
    Props(new UserAchievementActor(userId, newsRepository, drinksRepository))
  }

  case class ProcessDrinkNews(news: News)

}


class UserAchievementActor(userId: Int, newsRepository: NewsRepository, drinksRepository: DrinksRepository)(implicit ec: ExecutionContext) extends Actor with Stash {

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
      increaseCounters(news)
      Logger.info(achievementMetrics.checkAchievements.toString())
    case _ => Logger.info("huh?")
  }

  def increaseCounters(news:News) = {
    import Property._
    achievementMetrics.addValue(List(BEERCOUNT_HIGHER_THAN_5.name),news.cardinality)
  }
  def initializeAchievementMetrics = {
    newsRepository
      .getStatsForUser(userId)
      .map(initAchievements)
  }

  private def initAchievements(stats: NewsStats) = {
    import Property._
    achievementMetrics.defineProperty(BEERCOUNT_HIGHER_THAN_5.copy(initialValue = stats.beerCount.getOrElse(0)))
    achievementMetrics.defineAchievement(Achievement("Lenny", List(BEERCOUNT_HIGHER_THAN_5.name)))
  }

  case class InitializationDone()

}