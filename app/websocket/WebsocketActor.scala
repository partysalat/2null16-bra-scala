package websocket

import akka.actor._
import news.models.NewsWithItems
import play.api.libs.json.Json


object WebsocketActor {
  def props(manager:WebsocketManager,out: ActorRef) = Props(new WebsocketActor(manager,out))
  case class NotifyNews(newsWithItems:List[NewsWithItems])
  case class NotifyNewsRemove(newsId:Int)
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
    case NotifyNewsRemove(newsId)=>
      out ! Json.toJson(Json.arr("news.delete",newsId)).toString()
    case msg: String =>
      out ! "4"
  }
}