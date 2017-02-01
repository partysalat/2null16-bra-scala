package websocket

import akka.actor.ActorSystem
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import news.repos.NewsRepository
import websocket.WebsocketActor.NotifyNews

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WebsocketService @Inject()( @Named("websocketSystem") implicit val system: ActorSystem, newsRepository: NewsRepository)
                                (implicit ec: ExecutionContext) {


  def notify(newsIds:Seq[Int]): Future[Unit] = {
    newsRepository.getNewsByIds(newsIds)
      .map(newsWithItems=>
        //system.actorSelection(system / "*").tell(NotifyNews(newsWithItems))
        system.actorSelection(system / "*").
      )
  }
}
