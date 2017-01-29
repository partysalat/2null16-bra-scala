package services

import java.util.concurrent.TimeUnit

import actors.UserAchievementActor
import actors.UserAchievementActor.ProcessDrinkNews
import akka.actor.ActorSystem
import akka.util.Timeout
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import models.News
import repos.achievements.AchievementsRepository
import repos.drinks.DrinksRepository
import repos.news.NewsRepository

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@Singleton
class AchievementService @Inject()(
  @Named("userAchievementSystem") system: ActorSystem,
  newsRepository: NewsRepository,
  drinksRepository: DrinksRepository,
  achievementsRepository: AchievementsRepository
  )(implicit ec:ExecutionContext){
  implicit val timeout = Timeout(20,TimeUnit.SECONDS)

  def notifyAchievements(newsList:List[News]) = newsList.filter(_.userId.isDefined).foreach(notifyAchievement)

  private def notifyAchievement(news:News) = {
    val actorId = news.userId.get.toString
    sendMessage(actorId,news)
  }

  private def sendMessage(actorId:String,news:News)={
    val message = ProcessDrinkNews(news)
    system.actorSelection(system / actorId).resolveOne().onComplete({
      case Success(actor) => actor ! message
      case Failure(ex) =>
        val actor = system.actorOf(UserAchievementActor.props(news.userId.get,newsRepository,drinksRepository,achievementsRepository), name = actorId)
        actor ! message
    })
  }
}
