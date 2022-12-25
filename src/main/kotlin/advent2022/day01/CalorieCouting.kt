package advent2022.day01

import comon.puzzleInputFile

data class Elf(var calories: Int)

fun calorieCounting(): List<Elf> {
    var currentElf = Elf(0)
    val elves = mutableListOf(currentElf)
    puzzleInputFile(2022, "day1.txt").forEachLine { line ->
        if (line.isBlank()) {
            currentElf = Elf(0)
            elves.add(currentElf)
        } else {
            currentElf.calories += line.toInt()
        }
    }

    return elves
}

fun part1() {
    val elves = calorieCounting()
    println("max calories is ${elves.maxOf { it.calories }}")
}

fun part2() {
    calorieCounting().sortedByDescending { it.calories }.let { (first, second, third) ->
        println("top three = ${(first.calories + second.calories + third.calories)}")
    }
}

fun main() {
    part1()
    part2()
}
