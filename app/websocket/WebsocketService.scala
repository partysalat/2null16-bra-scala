package websocket

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import news.repos.NewsRepository
import websocket.WebsocketActor.{NotifyNews, NotifyNewsRemove}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WebsocketService @Inject()( @Named("websocketSystem") implicit val system: ActorSystem,
                                  newsRepository: NewsRepository,
                                  manager: WebsocketManager
                                )
                                (implicit ec: ExecutionContext) {
  implicit val timeout = Timeout(20, TimeUnit.SECONDS)
  def notify(newsIds:Seq[Int]): Future[Unit] = {
    newsRepository.getNewsByIds(newsIds)
      .map(newsWithItems=>
        manager.broadcast(NotifyNews(newsWithItems))
      )
  }

  def notifyNewsRemove(newsId:Int): Unit ={
    manager.broadcast(NotifyNewsRemove(newsId))
  }
}
