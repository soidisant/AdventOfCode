package adventofcode.year2022.day01

import adventofcode.comon.puzzleInputFile

data class Elf(var calories: Int)

fun calorieCounting(): List<adventofcode.year2022.day01.Elf> {
    var currentElf = adventofcode.year2022.day01.Elf(0)
    val elves = mutableListOf(currentElf)
    puzzleInputFile(2022, "day1.txt").forEachLine { line ->
        if (line.isBlank()) {
            currentElf = adventofcode.year2022.day01.Elf(0)
            elves.add(currentElf)
        } else {
            currentElf.calories += line.toInt()
        }
    }

    return elves
}

fun part1() {
    val elves = adventofcode.year2022.day01.calorieCounting()
    println("max calories is ${elves.maxOf { it.calories }}")
}

fun part2() {
    adventofcode.year2022.day01.calorieCounting().sortedByDescending { it.calories }.let { (first, second, third) ->
        println("top three = ${(first.calories + second.calories + third.calories)}")
    }
}

fun main() {
    adventofcode.year2022.day01.part1()
    adventofcode.year2022.day01.part2()
}
