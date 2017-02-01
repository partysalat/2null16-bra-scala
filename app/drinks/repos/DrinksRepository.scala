package drinks.repos

import com.google.inject.{Inject, Singleton}
import drinks.models.Drink
import drinks.models.DrinkType.DrinkType
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class DrinksRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends DrinksTable with HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._
  def insert(item: Drink): Future[Int] = db.run {
    drinksInc += item
  }
  def insertAll(items: List[Drink]) = db.run {
    drinksInc ++= items
  }
  def getAll(drinkType:DrinkType): Future[List[Drink]] = db.run {
    drinks.filter(_.drinkType === drinkType).to[List].result
  }
  def getById(id:Int): Future[Drink] = db.run {
    drinks.filter(_.id === id).result.head
  }
}


