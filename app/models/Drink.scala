package models

import models.DrinkType.DrinkType
import org.joda.time.DateTime

object DrinkType extends Enumeration {
  type DrinkType = Value
  val COCKTAIL = Value("COCKTAIL")
  val SHOT = Value("SHOT")
  val BEER = Value("BEER")
  val COFFEE = Value("COFFEE")
  val SOFTDRINK = Value("SOFTDRINK")
}

case class Drink(
                  name: String,
                  `type`: DrinkType,
                  id: Option[Int] = None,
                  createdAt: DateTime = DateTime.now(),
                  updatedAt: DateTime = DateTime.now()
                )