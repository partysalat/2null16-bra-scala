package actors

import akka.actor.{Actor, Props, Stash}
import models.News
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

object UserAchievementActor {
  def props(userId:Int)(implicit executionContext: ExecutionContext) = {
    Props(new UserAchievementActor(userId))
  }
  case class ProcessDrinkNews(news: News)
}


class UserAchievementActor(userId:Int)(implicit ec:ExecutionContext) extends Actor with Stash {
  import UserAchievementActor._

  override def preStart = {
    val future = Future.successful {Thread.sleep(2000); true}
    future.onSuccess({
      case _ => self ! InitializationDone
    })
  }
  def receive = initialReceive

  def initialReceive:Receive = {
    case InitializationDone =>{
      context.become(normalReceive)
      unstashAll()
    }
    case _ => stash()
  }

  def normalReceive:Receive = {
    case ProcessDrinkNews(news) =>
      Logger.info(s"User $userId has drink with id ${news.drinkId}")
    case _ => Logger.info("huh?")
  }


  case class InitializationDone()
}