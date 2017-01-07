package bootstrap

import com.google.inject.Inject
import models._
import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repos.achievements.AchievementsRepository
import repos.drinks.DrinksRepository
import repos.news.NewsRepository
import repos.users.UsersRepository

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
    _ <- newsRepository.insert(Data.news.head)

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
  val achievements = List(
    Achievement("Der frühe Vogel","Früh trinken","Mein Bild")
  )
  val drinks = List(
    Drink("Radeberger",DrinkType.BEER)
  )
  val news = List(News(1,NewsType.DRINK,drinkId = Some(1)))
}