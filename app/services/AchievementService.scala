package services

import java.util.concurrent.TimeUnit

import actors.UserAchievementActor
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import models.News

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@Singleton
class AchievementService @Inject()(@Named("userAchievementSystem") system: ActorSystem)(implicit ec:ExecutionContext){
  implicit val timeout = Timeout(5,TimeUnit.SECONDS)

  def notifyAchievements(newsList:List[News]) = newsList.filter(_.userId.isDefined).foreach(notifyAchievement)

  private def notifyAchievement(news:News) = {
    val actorId = news.userId.get.toString
    sendMessageIfActorExists(actorId,news)
  }

  private def sendMessageIfActorExists(actorId:String,news:News)={
    system.actorSelection(system / actorId).resolveOne().onComplete({
      case Success(actor) => actor ! "hello"
      case Failure(ex) =>
        val actor = system.actorOf(Props[UserAchievementActor], name = actorId)
        actor ! "hello"
    })
  }
}
