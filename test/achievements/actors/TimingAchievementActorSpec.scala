package achievements.actors

import achievements.actors.TimingAchievementActor.ProcessTimingAchievement
import achievements.models.{Achievement, TimingAchievementConstraints}
import achievements.services.AchievementService
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import news.services.NewsService
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import users.models.User

import scala.concurrent.Future
class TimingAchievementActorSpec
    extends TestKit(ActorSystem("MySpec"))
    with ImplicitSender
    with WordSpecLike
    with BeforeAndAfterAll
    with BeforeAndAfter
    with OneInstancePerTest
    with Matchers
//    with AsyncMockFactory
    with MockFactory {

  import drinks.models.DrinkType._
  trait Test {
    val anyTimingAchievementConstraint = TimingAchievementConstraints(
      Achievement(
        "Der letzte Kunde",
        "Letztes Bier vor 8 Uhr morgens bestellt",
        "/internal/assets/achievements/moe.png"
      ),
      pattern = "0 8 * * *",
      drinkType = BEER
    )

    val achievementServiceMock: AchievementService = stub[AchievementService]
    val newsServiceMock: NewsService = stub[NewsService]
    val actorRef = system.actorOf(
      Props(
        new TimingAchievementActor(newsServiceMock, achievementServiceMock)))
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "TimingAchievementActor " when {
    "process users" in new Test {
      val user1 = User(name = "Foo", id = Some(1))
      val user2 = User(name = "Foo2", id = Some(2))
      val users = Seq(Some(user1), Some(user2))
      (newsServiceMock.getLatestUserWithType _)
        .when(anyTimingAchievementConstraint.drinkType, *)
        .returning(Future.successful(users))
      (newsServiceMock.hasAchievementReached _)
        .when(anyTimingAchievementConstraint.achievement, user1.id.get)
        .returning(Future.successful(false))

      (newsServiceMock.hasAchievementReached _)
        .when(anyTimingAchievementConstraint.achievement, user2.id.get)
        .returning(Future.successful(true))
      (achievementServiceMock.unlockAchievements _)
        .when(*, *)
        .returning(Future.successful((): Unit))

      actorRef ! ProcessTimingAchievement(anyTimingAchievementConstraint)

      expectMsg("ACK")

      awaitAssert {
        (newsServiceMock.getLatestUserWithType _).verify(BEER, *)
      }
      awaitAssert {
        (newsServiceMock.hasAchievementReached _)
          .verify(anyTimingAchievementConstraint.achievement, user1.id.get)
      }
      awaitAssert {
        (achievementServiceMock.unlockAchievements _).verify(
          user1.id.get,
          Seq(anyTimingAchievementConstraint.achievement))
      }
      awaitAssert {
        (achievementServiceMock.unlockAchievements _)
          .verify(user2.id.get,
                  Seq(anyTimingAchievementConstraint.achievement))
          .never
      }

    }
  }
}
