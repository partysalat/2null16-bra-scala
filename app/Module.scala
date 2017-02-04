import com.google.inject.{AbstractModule, Provides, Singleton}
import java.time.Clock

import com.typesafe.config.{Config, ConfigFactory}

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
  }
  @Provides()
  @Singleton()
  def getConfig: Config = {
    ConfigFactory.load
  }

}
