package achievements.actors

import achievements.actors.AchievementComparator.AchievementComparator
import achievements.actors.AchievementCounterType.AchievementCounterType
import achievements.actors.AchievementDrinkType.AchievementDrinkType
import achievements.models.Achievement
import drinks.models.DrinkType.DrinkType
import play.api.Logger

import scala.collection.mutable
import scala.collection.mutable.Map

case class AchievementMetrics(
                               properties: Map[String, Property] = Map(),
                               achievements: Map[String, AchievementConstraints] = Map()
                             ) {

  import AchievementComparator._

  def defineProperty(property: Property) = {
    properties(property.name) = property
  }

  def defineAchievement(achievement: AchievementConstraints) = {
    achievements(achievement.achievement.name) = achievement
  }

  def nameMatchesPropertyDrinkName(name: Option[String], property: Property): Boolean = {
    val result = for {
      propertyDrinkName <- property.customDrinkName
      drinkName <- name
    } yield drinkName.contains(propertyDrinkName)
    result.getOrElse(true)
  }

  def setValue(propertyName: String, value: Int, name: Option[String]): Unit = {
    properties.get(propertyName)
      .filter(nameMatchesPropertyDrinkName(name, _))
      .map { property: Property =>
        property.activation match {
          case ACTIVE_IF_GREATER_THAN => if (value > property.value) value else property.value
          case ACTIVE_IF_LESS_THAN => if (value < property.value) value else property.value
          case _ => value
        }
      }.foreach { (newValue: Int) =>
      properties(propertyName).value = newValue
    }
  }

  def setValues(propertyNames: List[String], value: Int, name: Option[String] = None) = {
    propertyNames.foreach(propertyName => setValue(propertyName, value, name))
  }

  def addValues(propertyNames: List[String], value: Int, name: Option[String] = None) = {
    propertyNames.foreach(propertyName => setValue(propertyName, getValue(propertyName) + value, name))
  }

  def getValue(propertyName: String) = {
    properties(propertyName).value
  }

  private def unlockAchievement(achievement: AchievementConstraints) = {
    achievement.unlocked = true
  }

  def checkAchievements: List[AchievementConstraints] = {
    val unlockedAchievements = achievements
      .toList.map(_._2)
      .filter(!_.unlocked)
      .filter(achievement =>
        achievement.props.count(properties(_).isActive) == achievement.props.size
      )
    unlockedAchievements.foreach(unlockAchievement)
    Logger.info(unlockedAchievements.toString)
    unlockedAchievements
  }

  def unlockReachedAchievements(reachedAchievements: List[Achievement]): Unit = {
    val reachedAchievementNames = reachedAchievements.map(_.name)
    achievements
      .filter(ac => reachedAchievementNames.contains(ac._1))
      .foreach {
        case (_, achievementConstraint) => unlockAchievement(achievementConstraint)
      }
  }
}

case class AchievementConstraints(
                                   achievement: Achievement,
                                   props: List[String],
                                   var unlocked: Boolean = false
                                 )


object AchievementDrinkType extends Enumeration {
  type AchievementDrinkType = Value
  //user specific metrics
  val DRINK_COUNT = Value("DRINK_COUNT")
  val BEER = Value("BEER")
  val COCKTAIL = Value("COCKTAIL")
  val SHOT = Value("SHOT")
  val SOFTDRINK = Value("SOFTDRINK")
  val CUSTOM = Value("CUSTOM")
}

object AchievementCounterType extends Enumeration {
  type AchievementCounterType = Value
  //user specific metrics
  val USER = Value("USER")

  // sum metrics
  val ALL = Value("DRINK_COUNT_ALL")

  //Count at once
  val AT_ONCE = Value("DRINK_COUNT_AT_ONCE")

  implicit class DrinkTypeToAchievementCounterType(drinkType: DrinkType) {
    def toCounterType = {
      AchievementDrinkType.withName(drinkType.toString)
    }
  }

}

object AchievementComparator extends Enumeration {
  type AchievementComparator = Value
  val ACTIVE_IF_GREATER_THAN = Value(">")
  val ACTIVE_IF_LESS_THAN = Value("<")
  val ACTIVE_IF_EQUALS_TO = Value("=")
}

object Property {

  import AchievementComparator._

  implicit class HigherThan(types: (AchievementDrinkType, AchievementCounterType)) {
    def countHigherOrEqualThan(count: Int) = {
      Property.countHigherThanOrEqual(types._1, types._2, count)
    }

    def countEquals(count: Int) = {
      Property.countEqual(types._1, types._2, count)
    }
  }

  implicit class HigherThanCustom(types: (String, AchievementCounterType)) {
    def countHigherOrEqualThan(count: Int) = {
      Property.countHigherThanOrEqual(AchievementDrinkType.CUSTOM, types._2, count, Some(types._1))
    }

    def countEquals(count: Int) = {
      Property.countEqual(AchievementDrinkType.CUSTOM, types._2, count, Some(types._1))
    }
  }

  def countHigherThanOrEqual(drinkType: AchievementDrinkType, counterType: AchievementCounterType, number: Int, customDrinkName: Option[String] = None) = {
    val counterName = s"""${drinkType.toString}${customDrinkName.getOrElse("")}${counterType.toString}HigherThan$number"""
    countProperties(drinkType)(counterType)(counterName) = toProperty(counterName, number, ACTIVE_IF_GREATER_THAN, customDrinkName)
    counterName
  }

  def countEqual(drinkType: AchievementDrinkType, counterType: AchievementCounterType, number: Int, customDrinkName: Option[String] = None) = {
    val counterName = s"${drinkType.toString}${customDrinkName.getOrElse("")}${counterType.toString}Equals$number"
    countProperties(drinkType)(counterType)(counterName) = toProperty(counterName, number, ACTIVE_IF_EQUALS_TO, customDrinkName)
    counterName
  }

  private def toProperty(counterName: String, activationCount: Int, comparator: AchievementComparator, customDrinkName: Option[String]): Property = {
    Property(counterName, 0, comparator, activationCount, customDrinkName = customDrinkName)
  }

  val countProperties: mutable.Map[AchievementDrinkType, mutable.Map[AchievementCounterType, mutable.Map[String, Property]]] = initPropertyCount

  def initPropertyCount: mutable.Map[AchievementDrinkType, mutable.Map[AchievementCounterType, mutable.Map[String, Property]]] = {

    val tmp0 = AchievementDrinkType.values.toSeq
      .map(drinkType =>
        (
          drinkType,
          mutable.Map[AchievementCounterType, mutable.Map[String, Property]]() ++ AchievementCounterType.values.toSeq.map((_, mutable.Map[String, Property]())).toMap
        )
      ).toMap
    mutable.Map() ++ tmp0
  }
}

case class Property(
                     name: String,
                     initialValue: Int,
                     activation: AchievementComparator,
                     activationValue: Int,
                     var value: Int = 0,
                     customDrinkName: Option[String]
                   ) {

  import AchievementComparator._

  def isActive: Boolean = {
    activation match {
      case ACTIVE_IF_GREATER_THAN => value >= activationValue
      case ACTIVE_IF_LESS_THAN => value <= activationValue
      case ACTIVE_IF_EQUALS_TO => value == activationValue
    }
  }
}