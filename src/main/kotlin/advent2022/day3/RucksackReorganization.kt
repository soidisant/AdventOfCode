package advent2022.day3

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

val Char.priority
    get() = if (isUpperCase()) {
        code - 38
    } else {
        code - 96
    }

fun part1() {
    var sum = 0
    File(ClassLoader.getSystemResource("2022/day3.txt").file).let { file ->
        file.forEachLine { line ->
            val firstCompartment = line.substring(0, line.length / 2).toSet()
            val secondCompartment = line.substring(line.length / 2).toSet()
            sum += firstCompartment.intersect(secondCompartment).firstOrNull()?.priority ?: 0
        }
    }
    println("sum = $sum")
}

fun part2() {
    val file = File(ClassLoader.getSystemResource("2022/day3.txt").file)
    val bufferedReader = BufferedReader(InputStreamReader(FileInputStream(file)))
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
