package websocket

import akka.actor._
import news.models.NewsWithItems
import play.api.libs.json.Json


object WebsocketActor {
  def props(manager:WebsocketManager,out: ActorRef) = Props(new WebsocketActor(manager,out))
  case class NotifyNews(newsWithItems:List[NewsWithItems])
}

class WebsocketActor(manager: WebsocketManager,out: ActorRef) extends Actor {
  import WebsocketActor._

  override def preStart(): Unit = {
    manager.register(self)
  }
  override def postStop() = {
    manager.unregister(self)
  }

  def receive = {
    case NotifyNews(newsWithItems)=>
      out ! Json.toJson(Json.arr("news",newsWithItems)).toString()
    case msg: String =>
      out ! ("I received your message: " + msg)
  }
}