package repos.drinks


import com.google.inject.{Inject, Singleton}
import models.Drink
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class DrinksRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends DrinksTable with HasDatabaseConfigProvider[JdbcProfile] {

  def insert(item: Drink): Future[Int] = db.run {
    tableQueryInc += item
  }
  def insertAll(items: List[Drink]) = db.run {
    tableQueryInc ++= items
  }
}


