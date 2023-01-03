package advent2020.day10

import comon.puzzleInputBufferedReader

fun part1() {
    val adapters = mutableListOf<Int>(0)
    puzzleInputBufferedReader(2020, "day10.txt").forEachLine {
        adapters.add(it.toInt())
    }
    adapters.sort()
    adapters.add(adapters.last() + 3)
    var oneJolt = 0
    var threeJolt = 0
    adapters.zipWithNext().forEach { (a, b) ->
        if (b - a == 1) oneJolt++
        if (b - a == 3) threeJolt++
    }

    println(oneJolt * threeJolt)
}

fun part2() {
    val adapters = mutableListOf<Int>(0)
    puzzleInputBufferedReader(2020, "day10.txt").forEachLine {
        adapters.add(it.toInt())
    }
    adapters.sort()
    adapters.add(adapters.last() + 3)
}

fun main() {
    part1()
}
