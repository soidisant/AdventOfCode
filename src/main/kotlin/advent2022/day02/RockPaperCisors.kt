package advent2022.day02

import comon.puzzleInputFile

enum class Weapon(val score: Int) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3);
}

val Weapon.losesTo
    get() = when (this) {
        Weapon.ROCK -> Weapon.PAPER
        Weapon.PAPER -> Weapon.SCISSORS
        Weapon.SCISSORS -> Weapon.ROCK
    }

val Weapon.winsAgainst
    get() = when (this) {
        Weapon.ROCK -> Weapon.SCISSORS
        Weapon.PAPER -> Weapon.ROCK
        Weapon.SCISSORS -> Weapon.PAPER
    }

operator fun Weapon.plus(other: Weapon): Int =
    score +
        if (this == other) {
            3
        } else if (winsAgainst == other) {
            6
        } else {
            0
        }

fun parse(hand: String): Weapon = when (hand) {
    "A", "X" -> Weapon.ROCK
    "B", "Y" -> Weapon.PAPER
    "C", "Z" -> Weapon.SCISSORS
    else -> throw IllegalArgumentException("")
}

fun parsePart2(hand: String): Weapon = when (hand) {
    "A" -> Weapon.ROCK
    "B" -> Weapon.PAPER
    "C" -> Weapon.SCISSORS
    else -> throw IllegalArgumentException("")
}

fun Weapon.shouldPlay(outcome: String) = when (outcome) {
    "X" -> winsAgainst
    "Y" -> this
    "Z" -> losesTo
    else -> throw IllegalArgumentException("")
}

fun part1() {
    val reg = "\\s".toRegex()
    var score = 0
    puzzleInputFile(2022, "day2.txt").forEachLine { line ->
        line.takeIf { it.isNotEmpty() }?.split(reg)?.let { (opponent, me) ->
            score += parse(me) + parse(opponent)
        }
    }
    println("part 1 total score is $score")
}

fun part2() {
    val reg = "\\s".toRegex()
    var score = 0
    puzzleInputFile(2022, "day2.txt").forEachLine { line ->
        line.takeIf { it.isNotEmpty() }?.split(reg)?.let { (opponent, outcome) ->
            val opponentWeapon = parsePart2(opponent)
            score += opponentWeapon.shouldPlay(outcome) + opponentWeapon
        }
    }
    println("part 2 total score is $score")
}

fun main() {
    part1()
    part2()
}
