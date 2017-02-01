package websocket

import akka.actor.ActorSystem
import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}

class WebsocketModule extends AbstractModule {

  protected def configure: Unit = {
  }
  @Provides
  @Named("websocketSystem")
  def websocketSystem(): ActorSystem = ActorSystem("websocketSystemMmmm")

}