package controllers

import javax.inject.Singleton

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.Inject
import com.google.inject.name.Named
import play.api.mvc._
import play.api.libs.streams._
import websocket.WebsocketActor

@Singleton
class WebsocketController @Inject()(@Named("websocketSystem")implicit val system: ActorSystem, implicit val materializer: Materializer) {

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => WebsocketActor.props(out))
  }
}