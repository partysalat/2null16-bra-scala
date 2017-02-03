package achievements.actors

import achievements.actors.AchievementComparator.AchievementComparator
import achievements.actors.AchievementCounterType.AchievementCounterType
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

  def setValue(propertyName: String, value: Int): Unit = {
    properties.get(propertyName).map { property: Property =>
      property.activation match {
        case ACTIVE_IF_GREATER_THAN => if (value > property.value) value else property.value
        case ACTIVE_IF_LESS_THAN => if (value < property.value) value else property.value
        case _ => value
      }
    }.foreach { (newValue: Int) =>
      properties(propertyName).value = newValue
    }
  }

  def setValues(propertyNames: List[String], value: Int) = {
    propertyNames.foreach(propertyName => setValue(propertyName, value))
  }

  def addValues(propertyNames: List[String], value: Int) = {
    propertyNames.foreach(propertyName => setValue(propertyName, getValue(propertyName) + value))
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
  def unlockReachedAchievements(reachedAchievements:List[Achievement]): Unit ={
    val reachedAchievementNames = reachedAchievements.map(_.name)
    achievements
      .filter(ac=>reachedAchievementNames.contains(ac._1))
      .foreach {
        case (_, achievementConstraint) => unlockAchievement(achievementConstraint)
      }
  }
}

case class AchievementConstraints(
                        achievement:Achievement,
                        props: List[String],
                        var unlocked: Boolean = false
                      )
object AchievementCounterType extends Enumeration {
  type AchievementCounterType = Value
  //user specific metrics
  val DRINK_COUNT = Value("DRINK_COUNT")
  val BEER = Value("BEER")
  val COCKTAIL = Value("COCKTAIL")
  val SHOT = Value("SHOT")
  val SOFTDRINK = Value("SOFTDRINK")

  // sum metrics
  val DRINK_COUNT_ALL = Value("DRINK_COUNT_ALL")
  val BEER_ALL = Value("BEER_ALL")
  val COCKTAIL_ALL = Value("COCKTAIL_ALL")
  val SHOT_ALL = Value("SHOT_ALL")
  val SOFTDRINK_ALL = Value("SOFTDRINK_ALL")

  //Count at once
  val DRINK_COUNT_AT_ONCE = Value("DRINK_COUNT_AT_ONCE")
  val BEER_AT_ONCE = Value("BEER_AT_ONCE")
  val SHOT_AT_ONCE = Value("SHOT_AT_ONCE")
  val COCKTAIL_AT_ONCE = Value("COCKTAIL_AT_ONCE")
  val SOFTDRINK_AT_ONCE = Value("SOFTDRINK_AT_ONCE")

  implicit class DrinkTypeToAchievementCounterType(drinkType: DrinkType){
    def toCounterType = {
      AchievementCounterType.withName(drinkType.toString)
    }
    def toAllCounterType = {
      AchievementCounterType.withName(s"${drinkType.toString}_ALL")
    }

    def toAtOnceCounterType = {
      AchievementCounterType.withName(s"${drinkType.toString}_AT_ONCE")
    }
  }

}
object AchievementComparator extends Enumeration{
  type AchievementComparator = Value
  val ACTIVE_IF_GREATER_THAN = Value(">")
  val ACTIVE_IF_LESS_THAN = Value("<")
  val ACTIVE_IF_EQUALS_TO = Value("=")
}

object Property {
  import AchievementComparator._
  implicit class HigherThan(drinkType: AchievementCounterType){
    def countHigherOrEqualThan(count:Int) = {
      Property.countHigherThanOrEqual(drinkType,count)
    }
    def countEquals(count:Int) = {
      Property.countEqual(drinkType,count)
    }
  }


  def countHigherThanOrEqual(counterType:AchievementCounterType, number:Int)={
    val counterName = s"${counterType.toString}HigherThan$number"
    countProperties(counterType)(counterName) = toProperty(counterName,number,ACTIVE_IF_GREATER_THAN)

    counterName
  }

  def countEqual(counterType:AchievementCounterType, number:Int)={
    val counterName = s"${counterType.toString}Equals$number"
    countProperties(counterType)(counterName) = toProperty(counterName,number,ACTIVE_IF_EQUALS_TO)
    counterName
  }

  private def toProperty(counterName: String,activationCount: Int,comparator: AchievementComparator):Property = {
    Property(counterName,0,comparator,activationCount)
  }

  val countProperties: mutable.Map[AchievementCounterType.Value, mutable.Map[String, Property]] = initPropertyCount

  def initPropertyCount:mutable.Map[AchievementCounterType.Value, mutable.Map[String, Property]]= {
    val tmp1: Seq[(AchievementCounterType, mutable.Map[String, Property])] = AchievementCounterType.values.toSeq.map(counterType => (counterType,mutable.Map[String,Property]()))
    mutable.Map() ++ tmp1.toMap
  }
}

case class Property(
                     name: String,
                     initialValue: Int,
                     activation: AchievementComparator,
                     activationValue: Int,
                     var value: Int = 0
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