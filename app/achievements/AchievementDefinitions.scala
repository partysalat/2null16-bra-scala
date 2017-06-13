package achievements

import achievements.actors.Property
import achievements.models.{Achievement, AchievementConstraints}

object AchievementDefinitions {

  import Property._
  import actors.AchievementCounterType._
  import actors.AchievementDrinkType._

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
      List((BEER, USER) countHigherOrEqualThan 1)
    ),
    AchievementConstraints(
      Achievement(
        "Lenny",
        "5 Bier bestellt",
        "/internal/assets/achievements/lenny.png"
      ),
      List((BEER, USER) countHigherOrEqualThan 5)
    ),
    AchievementConstraints(
      Achievement(
        "Carl",
        "10 Bier bestellt",
        "/internal/assets/achievements/carl.png"
      ),
      List((BEER, USER) countHigherOrEqualThan 10)
    ),
    AchievementConstraints(
      Achievement(
        "Homer",
        "15 Bier bestellt",
        "/internal/assets/achievements/homer.png"
      ),
      List((BEER, USER) countHigherOrEqualThan 15)
    ),

    AchievementConstraints(
      Achievement(
        "Barney",
        "25 Bier bestellt",
        "/internal/assets/achievements/barney.png"
      ),
      List((BEER, USER) countHigherOrEqualThan 25)
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
      List((COCKTAIL, USER) countHigherOrEqualThan 1)
    ),
    AchievementConstraints(
      Achievement(
        "Hemingway",
        "5 Cocktails bestellt",
        "/internal/assets/achievements/hemingway.jpg"
      ),
      List((COCKTAIL, USER) countHigherOrEqualThan 5)
    ),
    AchievementConstraints(
      Achievement(
        "Churchill",
        "10 Cocktails bestellt",
        "/internal/assets/achievements/churchill.jpg"
      ),
      List((COCKTAIL, USER) countHigherOrEqualThan 10)
    ),
    AchievementConstraints(
      Achievement(
        "George R.R. Martin",
        "15 Cocktails bestellt",
        "/internal/assets/achievements/georgerrmartin.jpg"
      ),
      List((COCKTAIL, USER) countHigherOrEqualThan 15)
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
      List((SOFTDRINK, USER) countHigherOrEqualThan 5)
    ),
    AchievementConstraints(
      Achievement(
        "Coca Cola Fanta Sprite",
        "Mindestens 10 Softdrinks bestellt",
        "/internal/assets/achievements/cocacolaFantaSprite.jpg"
      ),
      List((SOFTDRINK, USER) countHigherOrEqualThan 10)
    ),

    /**
      * Shotrunden
      */
    AchievementConstraints(
      Achievement(
        "Die nächste Runde geht auf mich",
        "Mindestens 10 Shots auf einmal bestellt",
        "/internal/assets/achievements/dienaechsterundegehtaufmich.jpg"
      ),
      List((SHOT, AT_ONCE) countHigherOrEqualThan 10)
    ),
    AchievementConstraints(
      Achievement(
        "'ne Runde für alle!",
        "Mindestens 20 Shots auf einmal bestellt",
        "/internal/assets/achievements/nerundefueralle.jpg"
      ),
      List((SHOT, AT_ONCE) countHigherOrEqualThan 20)
    ),
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
    AchievementConstraints(
      Achievement(
        "Glückspils",
        "25. Bier bestellt",
        "/internal/assets/achievements/glueckspils.png"
      ),
      List((BEER, ALL) countEquals 25)
    ),
    AchievementConstraints(
      Achievement(
        "Es geht seinen Gang",
        "50. Bier bestellt",
        "/internal/assets/achievements/esgehtseinengang.png"
      ),
      List((BEER, ALL) countEquals 50)
    ),
    AchievementConstraints(
      Achievement(
        "Veni Vidi Bieri",
        "100. Bier bestellt",
        "/internal/assets/achievements/venividibieri.png"
      ),
      List((BEER, ALL) countEquals 100)
    ), AchievementConstraints(
      Achievement(
        "Halbzeit",
        "150. Bier bestellt",
        "/internal/assets/achievements/halbzeit.png"
      ),
      List((BEER, ALL) countEquals 150)
    ), AchievementConstraints(
      Achievement(
        "This is Sparta!",
        "300. Bier bestellt",
        "/internal/assets/achievements/thisissparta.png"
      ),
      List((BEER, ALL) countEquals 300)
    ),

    /**
      * Mix
      */
    AchievementConstraints(
      Achievement(
        "Rauf und runter",
        "Jeweils ein Bier, Shot und Cocktail bestellt",
        "/internal/assets/achievements/raufUndRunter.jpg"
      ),
      List((BEER, USER) countHigherOrEqualThan 1, (SHOT, USER) countHigherOrEqualThan 1, (COCKTAIL, USER) countHigherOrEqualThan 1)
    ),
    AchievementConstraints(
      Achievement(
        "Abenteurer",
        "Jeweils fünf Biere, Shots und Cocktails bestellt",
        "/internal/assets/achievements/abenteurer.jpg"
      ),
      List((BEER, USER) countHigherOrEqualThan 5, (SHOT, USER) countHigherOrEqualThan 5, (COCKTAIL, USER) countHigherOrEqualThan 5)
    ),
    AchievementConstraints(
      Achievement(
        "Der Alles-Trinker",
        "Jeweils 10 Biere, Shots und Cocktails bestellt",
        "/internal/assets/achievements/derallestrinker.jpg"
      ),
      List((BEER, USER) countHigherOrEqualThan 10, (SHOT, USER) countHigherOrEqualThan 10, (COCKTAIL, USER) countHigherOrEqualThan 10)
    ),
    AchievementConstraints(
      Achievement(
        "Herrengedeck",
        "Nen Bier und nen Kurzen bestellt",
        "/internal/assets/achievements/herrengedeck.jpg"
      ),
      List((BEER, USER) countHigherOrEqualThan 1, (SHOT, USER) countHigherOrEqualThan 1)
    ),

    /**
      * Drink specific
      */

    AchievementConstraints(
      Achievement(
        "Luftalarm",
        "Mindestens 5 Berliner Luft bestellt",
        "/internal/assets/achievements/luftalarm.jpg"
      ),
      List(("Berliner Luft", AT_ONCE) countHigherOrEqualThan 5)
    ),
    AchievementConstraints(
      Achievement(
        "Zombieland",
        "Mindestens 3 Zombies bestellt",
        "/internal/assets/achievements/zombieland.jpg"
      ),
      List(("Zombie", USER) countHigherOrEqualThan 3)
    ),
    AchievementConstraints(
      Achievement(
        "...den trinkt man auf Long Island so",
        "Mindestens einen Long Island Iced Tea bestellt",
        "/internal/assets/achievements/dentrinktmanauflongislandso.jpg"
      ),
      List(("Long Island Iced Tea", USER) countHigherOrEqualThan 1)
    ),

    AchievementConstraints(
      Achievement(
        "Anwärter des B.R.A.rabischen Frühlings",
        "Einen Long Island Iced Tea, einen Zombie und ein Bier bestellt",
        "/internal/assets/achievements/pseudoadmin.png"
      ),
      List(
        ("Long Island Iced Tea", USER) countHigherOrEqualThan 1,
        ("Zombie", USER) countHigherOrEqualThan 1,
        (BEER, USER) countHigherOrEqualThan 1
      )
    )
  )
}