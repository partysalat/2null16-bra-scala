package bootstrap

import com.google.inject.Inject
import models.User
import org.joda.time.DateTime
import play.Logger
import repos.UsersRepository
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by ben on 03/12/2016.
  */
class InitialData @Inject()(usersRepository: UsersRepository) {
  def insert = for {
    users <- usersRepository.getAll() if users.isEmpty
    _ <- usersRepository.insertAll(Data.users)

  } yield {}

    try {
      Logger.info("DB initialization.................")
      Await.result(insert, Duration.Inf)
    } catch {
      case ex: Exception =>
        Logger.warn("Error in database initialization ", ex)
    }
}

object Data {
  val users = List(
    User("Flo", DateTime.now()),
    User("Ben", DateTime.now()),
    User("Benni", DateTime.now()),
    User("Paul", DateTime.now())
  )
}