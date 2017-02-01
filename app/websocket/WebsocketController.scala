package websocket

import javax.inject.Singleton

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.Inject
import com.google.inject.name.Named
import play.api.libs.streams._
import play.api.mvc._

@Singleton
class WebsocketController @Inject()(
                                     @Named("websocketSystem")implicit val system: ActorSystem,
                                     val manager: WebsocketManager,
                                     implicit val materializer: Materializer) {

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => WebsocketActor.props(manager,out))
  }
}