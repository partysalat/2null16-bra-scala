package services

import java.util.concurrent.TimeUnit

import actors.UserAchievementActor
import actors.UserAchievementActor.ProcessDrinkNews
import akka.actor.{ActorNotFound, ActorSystem}
import akka.util.Timeout
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import models.News
import repos.achievements.AchievementsRepository
import repos.drinks.DrinksRepository
import repos.news.NewsRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class AchievementService @Inject()(
                                    @Named("userAchievementSystem") system: ActorSystem,
                                    newsRepository: NewsRepository,
                                    drinksRepository: DrinksRepository,
                                    achievementsRepository: AchievementsRepository
                                  )(implicit ec: ExecutionContext) {
  implicit val timeout = Timeout(20, TimeUnit.SECONDS)

  def notifyAchievements(newsList: List[News]): Future[List[Unit]] = Future.sequence(newsList.filter(_.userId.isDefined).map(notifyAchievement))

  private def notifyAchievement(news: News): Future[Unit] = {
    val actorId = news.userId.get.toString
    sendMessage(actorId, news)
  }

  private def sendMessage(actorId: String, news: News): Future[Unit] = {
    val message = ProcessDrinkNews(news)
    system.actorSelection(system / actorId).resolveOne()
      .map({ actor => actor ! message })
      .recoverWith({
        case ActorNotFound(_) =>
          newsRepository.getStatsForUser(news.userId.get)
            .map { stats =>
              val actor = system.actorOf(UserAchievementActor.props(news.userId.get, stats, newsRepository, drinksRepository, achievementsRepository), name = actorId)
              actor ! message
            }
      })
  }
}
