package websocket

import akka.actor._
import news.models.NewsWithItems


object WebsocketActor {
  def props(out: ActorRef) = Props(new WebsocketActor(out))
  case class NotifyNews(newsWithItems:List[NewsWithItems])
}

class WebsocketActor(out: ActorRef) extends Actor {
  import WebsocketActor._
  def receive = {
    case NotifyNews(newsWithItems)=>
      out ! List("news",newsWithItems)
    case msg: String =>
      out ! ("I received your message: " + msg)
  }
}