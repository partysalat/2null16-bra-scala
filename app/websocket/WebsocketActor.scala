package websocket

import akka.actor._
import news.models.NewsWithItems
import play.api.libs.json.Json


object WebsocketActor {
  def props(manager:WebsocketManager,out: ActorRef) = Props(new WebsocketActor(manager,out)).withDispatcher("websocket-dispatcher")
  sealed trait Notification
  case class NotifyNews(newsWithItems:List[NewsWithItems]) extends Notification
  case class NotifyNewsRemove(newsId:Int) extends Notification
  case class NotifyReloadImage(base64Image:String) extends Notification
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
    case NotifyReloadImage(base64Image)=>
      out ! Json.toJson(Json.arr("image.reload",base64Image)).toString()
    case msg: String =>
      out ! "4"
  }
}