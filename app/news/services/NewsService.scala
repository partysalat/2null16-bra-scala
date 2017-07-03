package news.services

import achievements.models.Achievement
import com.google.inject.{Inject, Singleton}
import drinks.models.DrinkType.DrinkType
import news.repos.NewsRepository
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext

@Singleton
class NewsService @Inject()(
    newsRepository: NewsRepository
)(implicit ec: ExecutionContext) {

  def getLatestUserWithType(drinkType: DrinkType, date: DateTime) =
    newsRepository.getLatestUserWithType(drinkType, date)

  def hasAchievementReached(achievement: Achievement, userId: Int) =
    newsRepository.hasAchievementReached(achievement, userId)
}
