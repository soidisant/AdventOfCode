package adventofcode.year2020.day5

import java.io.File

fun rowOrColumn(length: Int, input: String): Int {
    var l = length
    var interval = Pair(0, length - 1)
    input.toCharArray().forEach {
        l /= 2
        if (it == 'F' || it == 'L') {
            interval = Pair(interval.first, l + interval.first - 1)
        } else if (it == 'B' || it == 'R') {
            interval = Pair(interval.first + l, interval.second)
        }
    }
    return interval.first
}

fun translate(partition: String): Pair<Int, Int> {
    return Pair(rowOrColumn(128, partition.substring(0, 7)), rowOrColumn(8, partition.substring(7)))
}

fun part1() {
    var maxSeatID = 0
    val file = File(Thread.currentThread().contextClassLoader.getResource("2020/day5input.txt")!!.path)
    file.forEachLine {
        val seatID = translate(it).let {
            it.first * 8 + it.second
        }
        if (seatID > maxSeatID)
            maxSeatID = seatID
    }
    println(maxSeatID)
}

fun Pair<Int, Int>.seatID() = first * 8 + second

fun main() {
    val file = File(Thread.currentThread().contextClassLoader.getResource("2020/day5input.txt")!!.path)
    var seatIDS = mutableListOf<Int>()
    file.forEachLine {
        seatIDS.add(translate(it).seatID())
    }
    //  val seatIDS = seats.filter { it.first != 0 && it.first != 127 }.map { it.seatID() }.sorted().toMutableList()
    seatIDS = seatIDS.sorted().toMutableList()
    var prev = seatIDS.removeAt(0)
    do {
        if (seatIDS.first() - prev == 2) {
            println("founds ${prev + 1}")
            break
        }
        prev = seatIDS.removeAt(0)
    } while (seatIDS.isNotEmpty())
}
