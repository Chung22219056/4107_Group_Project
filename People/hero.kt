package People

import ANSIColorConsole
import AttackCard
import Card
import Deck
import DodgeCard
import PeachCard
import Role
import heros
import mainEventManager
import java.util.ArrayDeque
import javax.swing.text.StyledEditorKit.BoldAction

enum class Gender {
    Male, Female;
}

interface Handler {
    fun setNext(h: Handler)
    fun handle(): Boolean
}

abstract class Hero(role: Role) : Handler {
    abstract var name: String;
    abstract var HP: Int;
    abstract var maxHP: Int;
    abstract var gender: Gender;
    abstract var cards: MutableList<Card>;
    var role: Role = role;
    open var weapons: Card? = null
    open var armor: Card? = null
    var judgmentZone = ArrayDeque<Card>()
    var judgmentFlag:Boolean = false
    var abandonRound: Boolean = false

    var canAttack: Boolean = true;
    open fun setJudgmentZone(card:Card){
        judgmentZone.push(card)
    }

    open fun getJudgmentZone():Boolean{
        if(!judgmentZone.isEmpty()) return true // need to have judgment
        return this.judgmentFlag
    }

    open fun drawPhase(hero: Hero) {
        hero.getCard(Deck.getRadomCard())
        hero.getCard(Deck.getRadomCard())
    }
    open fun getJudgement():Card{
        return  Deck.getRadomCard()
    }
    //Compulsory
    open fun askHeroPlaceACard(filterList: List<String>? = null): Card {
        var cardList = mutableListOf<Card>();
        cardList = if (filterList != null) {
            filterCardFromCards(filterList);
        } else {
            cards;
        }

        while (true) {
            println("Please place a card");
            this.displayCardFromList(cardList);
            var index = readLine()?.toInt(); //card of index
            if (index !== null && cards.size >= index && index!! > 0) {
                return cards[index - 1];
            }
            println("Not valid input, Please input again.");
            continue;
        }
    }

    //Selection
    open fun askHeroPlaceACardOrNot(filterList: List<String>? = null): Card? {
        var cardList = mutableListOf<Card>();
        cardList = if (filterList != null) {
            filterCardFromCards(filterList);
        } else {
            cards;
        }

        while (true) {
            println("Please place a card");
            this.displayCardFromList(cardList);
            println("0.[cancel place a card]");
            var index = readLine()?.toInt();

            if (index != 0 && cardList.size >= index!!) {
                return cardList[index - 1];
            } else if (index == 0) {
                break;
            }
            println("Not valid input, Please input again.");
            continue;
        }
        return null;
    }

    open fun hasDodgeTypeCard(): Boolean {
        for (card in cards) {
            if (card is DodgeCard || card.name == "Dodge") {
                return true;
            }
        }
        return false;
    }

    open fun hasAttackTypeCard(): Boolean {
        for (card in cards) {
            if (card is AttackCard || card.name == "Attack") {
                return true;
            }
        }
        return false;
    }

    open fun hasPeachTypeCard(): Boolean {
        for (card in cards) {
            if (card is PeachCard || card.name == "Peach") {
                return true;
            }
        }
        return false;
    }

    open fun displayCardFromList(cardList: List<Card>) {
        println("Card List: ");
        for ((index, card) in cardList.withIndex()) {
            println("${index + 1}.[${card.getCardString()}] ");
        }
    }

    open fun filterCardFromCards(filterList: List<String>? = null): MutableList<Card> {
        val filteredCardList = mutableListOf<Card>();
        if (filterList != null) {
            for (card in cards) {

                if (card.name in filterList) {
                    filteredCardList.add(card);
                }
            }
        }
        return filteredCardList;
    }

    open fun displayCards() {
        println("Card List: ");
        for ((index, card) in cards.withIndex()) {
            println("${index + 1}.[${card.getCardString()}] ");
        }
    }

    open fun showCurrentHP() {
        println("$name ${ANSIColorConsole.red("♥")} HP = ${HP}");
    }

    open fun getCard(card: Card) {
        cards.add(card);
    }

    open fun removeCard(card: Card) {
        cards.remove(card);
    }

    open fun showRoleList(heros: List<Hero>, currentHero: String) {
        for ((index, hero) in heros.withIndex()) {
            if (!hero.name.equals(currentHero))
                println("${index + 1}. ${hero.name}")
        }
    }

    open fun attackEventHandle(placedCard: Card) {
        println("Please select a hero you want to attack");

        //show list of hero
        var availableHeroes = listOf<Hero>();
        for ((index, hero) in heros.withIndex()) {
            if (hero != this) {
                println("${availableHeroes.size}. ${hero.name}");
                availableHeroes += hero;
            }
        }

        //selected by attacker
        var index = readlnOrNull()?.toInt();
        if (index != null) {
            mainEventManager.notifySpecificListener("Attack", this, availableHeroes[index], placedCard);
        }
    }

    open fun duelHandle(placedCard: Card) {
        println("Please select a hero you want to duel");

        //show list of hero
        var availableHeroes = listOf<Hero>();
        for ((index, hero) in heros.withIndex()) {
            if (hero != this) {
                println("${availableHeroes.size}. ${hero.name}");
                availableHeroes += hero;
            }
        }

        //selected by attacker
        var index = readlnOrNull()?.toInt();
        if (index != null) {
            mainEventManager.notifySpecificListener("Duel", this, availableHeroes[index], placedCard);
        }
    }

    open fun barbariansAssaultHandle(placedCard: Card){
        println("${this.name} use ${placedCard.name}, all hero need to use [Attack] card to dodge the hurt\n")

        var availableHeroes = listOf<Hero>();
        for ((index, hero) in heros.withIndex()) {
            if (hero != this) {
//                println("${availableHeroes.size}. ${hero.name}");
                availableHeroes += hero;

            }
        }
        mainEventManager.notifyAllHero("barbariansAssault", this,placedCard);


    }

    open fun HailofArrowsHandle(placedCard: Card){
        println("${this.name} use ${placedCard.name}, all hero need to use [Dodge] card to dodge the hurt\n")

        var availableHeroes = listOf<Hero>();
        for ((index, hero) in heros.withIndex()) {
            if (hero != this) {
                availableHeroes += hero;

            }
        }
        mainEventManager.notifyAllHero("hailofArrowsAssault", this,placedCard);


    }


    open fun acediaEventHandle(placedCard: Card) {
        println("Please select a hero you want to place the Acedia");

        //show list of hero
        var availableHeroes = listOf<Hero>();
        for ((index, hero) in heros.withIndex()) {
            if (hero != this) {
                println("${availableHeroes.size}. ${hero.name}");
                availableHeroes += hero;
            }
        }

        //selected by attacker
        var index = readlnOrNull()?.toInt();
        if (index != null) {
           availableHeroes[index].setJudgmentZone(placedCard)
        }
    }



    open fun attackEvent(placedCard: Card, heros: List<Hero>, currentHero: String) {
        println("Select you want to attack role")

        showRoleList(heros, currentHero)
        var commandInput = readLine();

        for ((index, hero) in heros.withIndex()) {
            if (commandInput.equals("${(index + 1).toString()}")) {
                println("You select attack ${hero.name}\n")

//                println("${hero.name} need to make a decision, please select a card:")
                dodgeEvent(heros, hero.name)
//                hero.displayCards()
//                commandInput = readLine();
            }
        }

//        mainEventManager.notifyListener("Attack", this, placedCard);
    }

    open fun dodgeEvent(heros: List<Hero>, beAttackedHero: String) {

        for ((index, hero) in heros.withIndex()) {

            if (hero.name.equals("${beAttackedHero}") && hero.cards.size > 0) {

                for ((index, card) in hero.cards.withIndex()) {

//                    if (card.name.equals("Dodge")) {
                    println("${hero.name}")
                    hero.displayCards()
                    print("0.[give up dodge]\n");

                    while (true) {
                        var commandInput = readLine();
                        if (commandInput == "0") {
                            println("${hero.name} get hurt hp -1")
                            hero.HP -= 1
                            println("${hero.name} ${ANSIColorConsole.red("♥")} HP = ${hero.HP}")
                            break
                        } else {
                            if (hero.cards[commandInput!!.toInt() - 1].name.equals("Dodge")) {
                                println(hero.cards[commandInput!!.toInt() - 1].name)
                                var cardPlaced = hero.cards[commandInput!!.toInt() - 1];
                                hero.removeCard(cardPlaced);
                                println("${hero.name} use dodge card to dodge attack")
                                break
                            } else {
                                println("You can not use this card ,please input command again")
                                continue
                            }

                        }
                    }
//                    }

                }


            } else if (hero.name.equals("${beAttackedHero}")) {
                println("${hero.name} no card dodge that get hurt hp -1")
                hero.HP -= 1
                println("${hero.name} ${ANSIColorConsole.red("♥")} HP = ${hero.HP}")
                break
            }
        }
    }


}

