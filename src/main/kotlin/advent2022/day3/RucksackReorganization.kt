package advent2022.day3

import comon.puzzleInputBufferedReader
import comon.puzzleInputFile

val Char.priority
    get() = if (isUpperCase()) {
        code - 38
    } else {
        code - 96
    }

fun part1() {
    var sum = 0
    puzzleInputFile(2022, "day3.txt").forEachLine { line ->
        val firstCompartment = line.substring(0, line.length / 2).toSet()
        val secondCompartment = line.substring(line.length / 2).toSet()
        sum += firstCompartment.intersect(secondCompartment).firstOrNull()?.priority ?: 0
    }
    println("sum = $sum")
}

fun part2() {
    val bufferedReader = puzzleInputBufferedReader(2022, "day3.txt")
    var sum = bufferedReader.lineSequence().chunked(3) { it.map(String::toSet) }.fold(0) { acc, rucksacks ->
        acc + (rucksacks.reduce(Iterable<Char>::intersect).firstOrNull()?.priority ?: 0)
    }
    bufferedReader.close()
    println("sum part 2 = $sum")
}

fun main() {
    part1()
    part2()
}
