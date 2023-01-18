package adventofcode.year2022.day04

import adventofcode.comon.puzzleInputBufferedReader

val lineRegex = "(\\d+)-(\\d+),(\\d+)-(\\d+)".toRegex()
fun parse(line: String) =
    lineRegex.matchEntire(line)?.let {
        IntRange(it.groupValues[1].toInt(), it.groupValues[2].toInt()) to
            IntRange(it.groupValues[3].toInt(), it.groupValues[4].toInt())
    } ?: throw IllegalArgumentException("")

fun fullyContains(r1: IntRange, r2: IntRange) = r1.first.compareTo(r2.first).let {
    if (it == 0) {
        true
    } else if (it < 0) {
        r2.last <= r1.last
    } else {
        r1.last <= r2.last
    }
}

fun overlaps(r1: IntRange, r2: IntRange) = r1.first.compareTo(r2.first).let {
    if (it == 0) {
        true
    } else if (it < 0) {
        r2.first <= r1.last
    } else {
        r1.first <= r2.last
    }
}

fun compareAssignments(transform: (IntRange, IntRange) -> Boolean): Int {
    val bufferedReader = puzzleInputBufferedReader(2022, "day4.txt")
    val sum = bufferedReader.lineSequence().fold(0) { acc, line ->
        parse(line).let { (r1, r2) ->
            acc + (if (transform(r1, r2)) 1 else 0)
        }
    }
    bufferedReader.close()
    return sum
}

fun main() {
    println("part 1 : ${compareAssignments(::fullyContains)} ")
    println("part 2 : ${compareAssignments(::overlaps)} ")
}
