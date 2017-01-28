package actors

import akka.actor.ActorSystem
import com.google.inject.Inject
import com.google.inject.name.Named
import play.Logger

class ActorInitializer @Inject()(@Named("userAchievementSystem") system: ActorSystem) {
  def initialize = {
    Logger.info("Achievement actors initializing....")
    Logger.info(system.toString)

  }

  initialize
}
