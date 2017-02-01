package websocket

import akka.actor.ActorRef
import com.google.inject.{Inject, Singleton}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WebsocketManager @Inject()()
                                (implicit ec: ExecutionContext) {

  val actorList:mutable.ListMap[String,ActorRef] = mutable.ListMap[String,ActorRef]()
  def register(actor:ActorRef): Unit = {
    actorList(actor.path.toString) = actor
  }
  def unregister(actor:ActorRef):Unit = {
    actorList.remove(actor.path.toString)
  }
  def broadcast(msg:AnyRef) ={
    actorList.foreach {
      case (_,actor)=>actor !msg
    }
  }
}
