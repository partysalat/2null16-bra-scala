package actors

import akka.actor.Actor
import play.api.Logger

class UserAchievementActor extends Actor {
  def receive = {
    case "hello" => Logger.info(s"hello back at you ${self.toString()}")
    case _ => Logger.info("huh?")
  }
}