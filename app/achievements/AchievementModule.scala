package achievements

import akka.actor.ActorSystem
import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}

class AchievementModule extends AbstractModule {

  protected def configure: Unit = {
  }
  @Provides
  @Named("userAchievementSystem")
  def userAchievementSystem(): ActorSystem = ActorSystem("userAchievementSystem")
}