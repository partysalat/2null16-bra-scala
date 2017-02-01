package bootstrap

import achievements.repos.AchievementsRepository
import achievements.AchievementDefinitions
import achievements.models.Achievement
import com.google.inject.Inject
import models._
import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repos.drinks.DrinksRepository
import repos.news.NewsRepository
import repos.users.UsersRepository

import scala.collection.immutable.Seq
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class InitialData @Inject()(
                             usersRepository: UsersRepository,
                             achievementsRepository: AchievementsRepository,
                             drinksRepository: DrinksRepository,
                             newsRepository: NewsRepository
                           ) {
  def insert = for {
    users <- usersRepository.getAll() if users.isEmpty
    _ <- usersRepository.insertAll(Data.users)
    _ <- achievementsRepository.insertAll(Data.achievements)
    _ <- drinksRepository.insertAll(Data.drinks)
    _ <- newsRepository.insertAll(Data.news)

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
    User("Flo"),
    User("Ben"),
    User("Benni"),
    User("Paul")
  )
  val achievements: List[Achievement] = AchievementDefinitions.achievements.map(_.achievement)
  val drinks = List(
    Drink("Radeberger",DrinkType.BEER)
  )
  val news = List()
}