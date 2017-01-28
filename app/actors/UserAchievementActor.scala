package actors

import akka.actor.{Actor, Props, Stash}
import models.DrinkType.DrinkType
import models.{DrinkType, News}
import play.api.Logger
import repos.drinks.DrinksRepository
import repos.news.NewsRepository

import scala.concurrent.{ExecutionContext, Future}

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
    case _ => Logger.info("huh?")
  }

  def initializeAchievementMetrics = {
    newsRepository
      .getStatsForUser(userId)
      .map(stats => {
        achievementMetrics.userDrinkCount = stats.drinkCount.getOrElse(0)
        achievementMetrics.drinkTypeCounts(DrinkType.COCKTAIL) = stats.cocktailCount.getOrElse(0)
        achievementMetrics.drinkTypeCounts(DrinkType.SHOT) = stats.shotCount.getOrElse(0)
        achievementMetrics.drinkTypeCounts(DrinkType.BEER) = stats.beerCount.getOrElse(0)
        achievementMetrics.drinkTypeCounts(DrinkType.COFFEE) = stats.coffeeCount.getOrElse(0)
        achievementMetrics.drinkTypeCounts(DrinkType.SOFTDRINK) = stats.softdrinkCount.getOrElse(0)
      }
      )
  }

  case class InitializationDone()

}