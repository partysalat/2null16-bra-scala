package achievements

import akka.actor.ActorSystem
import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides, Singleton}

class AchievementModule extends AbstractModule {

  protected def configure: Unit = {
  }
  @Provides
  @Singleton
  @Named("userAchievementSystem")
  def userAchievementSystem(): ActorSystem = ActorSystem("userAchievementSystem")
}