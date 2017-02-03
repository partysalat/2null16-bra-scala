package achievements

import achievements.actors.AchievementConstraints
import achievements.actors.Property
import achievements.models.Achievement

object AchievementDefinitions {

  import Property._
  import actors.AchievementCounterType._

  val achievements = List(
    /**
      * Beer
      */
    AchievementConstraints(
      Achievement(
        "Moe",
        "1 Bier bestellt",
        "/internal/assets/achievements/moe.png"
      ),
      List(BEER countHigherOrEqualThan 1)
    ),
    AchievementConstraints(
      Achievement(
        "Lenny",
        "5 Bier bestellt",
        "/internal/assets/achievements/lenny.png"
      ),
      List(BEER countHigherOrEqualThan 5)
    ),
    AchievementConstraints(
      Achievement(
        "Carl",
        "10 Bier bestellt",
        "/internal/assets/achievements/carl.png"
      ),
      List(BEER countHigherOrEqualThan 10)
    ),
    AchievementConstraints(
      Achievement(
        "Homer",
        "15 Bier bestellt",
        "/internal/assets/achievements/homer.png"
      ),
      List(BEER countHigherOrEqualThan 15)
    ),

    AchievementConstraints(
      Achievement(
        "Barney",
        "25 Bier bestellt",
        "/internal/assets/achievements/barney.png"
      ),
      List(BEER countHigherOrEqualThan 25)
    ),

    /**
      * Cocktails
      */

    AchievementConstraints(
      Achievement(
        "Jeff Lebowski",
        "1 Cocktails bestellt",
        "/internal/assets/achievements/derdude.jpg"
      ),
      List(COCKTAIL countHigherOrEqualThan 1)
    ),
    AchievementConstraints(
      Achievement(
        "Hemingway",
        "5 Cocktails bestellt",
        "/internal/assets/achievements/hemingway.jpg"
      ),
      List(COCKTAIL countHigherOrEqualThan 5)
    ),
    AchievementConstraints(
      Achievement(
        "Churchill",
        "10 Cocktails bestellt",
        "/internal/assets/achievements/churchill.jpg"
      ),
      List(COCKTAIL countHigherOrEqualThan 10)
    ),
    AchievementConstraints(
      Achievement(
        "George R.R. Martin",
        "15 Cocktails bestellt",
        "/internal/assets/achievements/georgerrmartin.jpg"
      ),
      List(COCKTAIL countHigherOrEqualThan 15)
    ),

    /**
      * Softdrinks
      */
    AchievementConstraints(
      Achievement(
        "Is this just Fanta Sea?",
        "Mindestens 5 Softdrinks bestellt",
        "/internal/assets/achievements/fantaSea.jpg"
      ),
      List(SOFTDRINK countHigherOrEqualThan 5)
    ),
    AchievementConstraints(
      Achievement(
        "Coca Cola Fanta Sprite",
        "Mindestens 10 Softdrinks bestellt",
        "/internal/assets/achievements/cocacolaFantaSprite.jpg"
      ),
      List(SOFTDRINK countHigherOrEqualThan 10)
    ),

    /**
      * Shotrunden
      */

    //Die nächste Runde geht auf mich
    //'ne Runde für alle!

    /**
      * Timing
      */
    //Der frühe Vogel trinkt Bier
    // Der abend kann kommen
    //Einer der letzten Kunden

    /**
      * Einmalig
      */
    //glueckspils
    AchievementConstraints(
      Achievement(
        "Glückspils",
        "25. Bier bestellt",
        "/internal/assets/achievements/glueckspils.png"
      ),
      List(BEER_ALL countEquals  25)
    ),
    AchievementConstraints(
      Achievement(
        "Es geht seinen Gang",
        "50. Bier bestellt",
        "/internal/assets/achievements/esgehtseinengang.png"
      ),
      List(BEER_ALL countEquals  50)
    ),
    AchievementConstraints(
      Achievement(
        "Veni Vidi Bieri",
        "100. Bier bestellt",
        "/internal/assets/achievements/venividibieri.png"
      ),
      List(BEER_ALL countEquals  100)
    ),AchievementConstraints(
      Achievement(
        "Halbzeit",
        "150. Bier bestellt",
        "/internal/assets/achievements/halbzeit.jpg"
      ),
      List(BEER_ALL countEquals  150)
    ),AchievementConstraints(
      Achievement(
        "This is Sparta!",
        "300. Bier bestellt",
        "/internal/assets/achievements/thisissparta.jpg"
      ),
      List(BEER_ALL countEquals 300)
    ),
    // es geht seinen gang
    // Veni Vidi Bieri
    // Halbzeit
    // This is Sparta
    /**
      * Mix
      */
    AchievementConstraints(
      Achievement(
        "Rauf und runter",
        "Jeweils ein Bier, Shot und Cocktail bestellt",
        "/internal/assets/achievements/raufUndRunter.jpg"
      ),
      List(BEER countHigherOrEqualThan 1, SHOT countHigherOrEqualThan 1, COCKTAIL countHigherOrEqualThan 1)
    ),
    AchievementConstraints(
      Achievement(
        "Abenteurer",
        "Jeweils fünf Biere, Shots und Cocktails bestellt",
        "/internal/assets/achievements/abenteurer.jpg"
      ),
      List(BEER countHigherOrEqualThan 5, SHOT countHigherOrEqualThan 5, COCKTAIL countHigherOrEqualThan 5)
    ),
    AchievementConstraints(
      Achievement(
        "Der Alles-Trinker",
        "Jeweils 10 Biere, Shots und Cocktails bestellt",
        "/internal/assets/achievements/derallestrinker.jpg"
      ),
      List(BEER countHigherOrEqualThan 10, SHOT countHigherOrEqualThan 10, COCKTAIL countHigherOrEqualThan 10)
    ),
    AchievementConstraints(
      Achievement(
        "Herrengedeck",
        "Nen Bier und nen Kurzen bestellt",
        "/internal/assets/achievements/herrengedeck.jpg"
      ),
      List(BEER countHigherOrEqualThan 1, SHOT countHigherOrEqualThan 1)
    )
    //Luftalarm
  )
}