package bootstrap

import achievements.{AchievementDefinitions, AchievementDefinitionsTiming}
import achievements.models.Achievement
import achievements.repos.AchievementsRepository
import com.google.inject.Inject
import drinks.models.{Drink, DrinkType}
import drinks.repos.DrinksRepository
import news.repos.NewsRepository
import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import users.models.User
import users.repos.UsersRepository

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

  import DrinkType._

  val users = List(
    User("Ben"),
    User("Thomas"),
    User("Winnii"),
    User("Flo"),
    User("Sophie"),
    User("Jan"),
    User("Benni F."),
    User("Andrej"),
    User("Conni Lohann"),
    User("Nasimausi"),
    User("Tina"),
    User("Simon"),
    User("Nils"),
    User("Albrecht"),
    User("Charly"),
    User("Noreen"),
    User("Kevin"),
    User("Stephan B"),
    User("Vivien"),
    User("Benni B."),
    User("Paul Boeck"),
    User("Meike"),
    User("Tim"),
    User("Felix"),
    User("Schröder"),
    User("Stephan Müller"),
    User("Tori"),
    User("Jenny Hessler"),
    User("Stephan Grunwald"),
    User("Katharina Kunkel"),
    User("Phil"),
    User("Paul Wilpert"),
    User("Caro"),
    User("Resi"),
    User("Olli"),
    User("Christine"),
    User("Saskia"),
    User("Jenny Ho"),
    User("Robert"),
    User("Franzi"),
    User("Ludwig"),
    User("Jens"),
    User("Dana"),
    User("Albrecht"),
    User("Rike"),
    User("Julia")

  )
  val achievements: List[Achievement] = List(
    AchievementDefinitions.achievements.map(_.achievement),
    AchievementDefinitionsTiming.achievements.map(_.achievement)
  ).flatten

  val drinks = List(
    Drink("Radeberger", BEER),
    Drink("Hasseröder", BEER),
    Drink("Bitburger", BEER),
    Drink("Lübzer Lemon", BEER),
    Drink("Franziskaner", BEER),
    Drink("Berliner Weiße", BEER),
    Drink("Bananenweizen", BEER),
    Drink("Warsteiner", BEER),

    Drink("Mojito", COCKTAIL),
    Drink("Caipirinha", COCKTAIL),
    Drink("Zombie", COCKTAIL),
    Drink("Erdbeer Daiquiri", COCKTAIL),
    Drink("Long Island Iced Tea", COCKTAIL),
    Drink("Mai Tai", COCKTAIL),
    Drink("Planters Punch", COCKTAIL),
    Drink("Tequila Sunrise", COCKTAIL),
    Drink("Wodka Sling", COCKTAIL),
    Drink("Springtime Cooler", COCKTAIL),
    Drink("Pineapple Fizz", COCKTAIL),
    Drink("White Lady", COCKTAIL),
    Drink("Tom Collins", COCKTAIL),
    Drink("LeGurk", COCKTAIL),
    Drink("Cuba Libre", COCKTAIL),
    Drink("Gin Tonic", COCKTAIL),
    Drink("Hugo", COCKTAIL),
    Drink("Screwdriver", COCKTAIL),
    Drink("Wodka Julep", COCKTAIL),
    Drink("Sonstiger Cocktail", COCKTAIL),
    Drink("Moscow Mule", COCKTAIL),
    Drink("White Russian", COCKTAIL),
    Drink("Sex in the Beach", COCKTAIL),
    Drink("Cola Whisky", COCKTAIL),
    Drink("Bloody Mary", COCKTAIL),
    Drink("Berliner Luft", SHOT),

    Drink("Wodka", SHOT),
    Drink("Tequila", SHOT),
    Drink("Polnische Rakede", SHOT),
    Drink("Mexikaner", SHOT),
    Drink("Sonstiger Shot", SHOT),
    Drink("Obstler", SHOT),

    Drink("Cola", SOFTDRINK),
    Drink("Fanta", SOFTDRINK),
    Drink("Sprite", SOFTDRINK),
    Drink("Mate", SOFTDRINK),
    Drink("Wasser", SOFTDRINK),
    Drink("Cocktail ohne alk", SOFTDRINK),
    Drink("Orangensaft", SOFTDRINK),
    Drink("Apfelsaft", SOFTDRINK),
    Drink("Tomatensaft", SOFTDRINK),
    Drink("Apfelsaft", SOFTDRINK),
    Drink("Spezi", SOFTDRINK)

  )
  val news = List()
}