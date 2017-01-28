package actors

import akka.actor.{Actor, Stash}
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

class UserAchievementActor(userId:Int)(implicit ec:ExecutionContext) extends Actor with Stash {
  override def preStart = {
    val future = Future.successful {Thread.sleep(2000); true}
    future.onSuccess({
      case _ => self ! InitializationDone
    })
  }

  def initialReceive:Receive = {
    case InitializationDone =>{
      context.become(normalReceive)
      unstashAll()
    }
    case _ => stash()
  }

  def normalReceive:Receive = {
    case "hello" => Logger.info(s"hello back at you ${self.toString()}")
    case _ => Logger.info("huh?")
  }

  def receive = initialReceive
  case class InitializationDone()
}