package advent2020.day9

import comon.puzzleInputBufferedReader

const val preamble = 25

fun isValid(num: Long, set: List<Long>): Boolean {
    if (set.size < 2) {
        return false
    }
    for (first in set.indices) {
        for (second in IntRange(first + 1, set.indices.last)) {
            val compare = (set.elementAt(first) + set.elementAt(second)).compareTo(num)
            if (compare == 0) {
                return true
            }
            if (compare < 0) {
                break
            }
        }
    }
    return false
}

fun part1(): Long {
    val list = mutableListOf<Long>()
    puzzleInputBufferedReader(2020, "day9.txt").buffered().useLines {
        it.forEach { row ->
            val line = row.toLong()
            if (list.size < preamble) {
                list.add(line)
            } else {
                if (isValid(line, list.sortedDescending())) {
                    list.removeFirst()
                    list.add(line)
                } else {
                    println("Part1 : $line is not valid")
                    return line
                }
            }
        }
    }
    throw NoSuchElementException()
}

fun part2(weakness: Long) {
    val list = mutableListOf<Long>()
    var sum = 0L
    puzzleInputBufferedReader(2020, "day9.txt").buffered().use { reader ->
        while (sum != weakness && reader.ready()) {
            reader.readLine().toLong().also {
                list.add(it)
                sum += it
            }
            while (sum > weakness && list.isNotEmpty()) {
                sum -= list.removeAt(0)
            }
        }
        if (sum == weakness && list.size > 1) {
            val result = (list.maxOrNull() ?: 0) + (list.minOrNull() ?: 0)
            println("Part2 : encryption weakness is $result")
        } else {
            println("Part2 : no encryption weakness found")
        }
    }
}

fun main() {
    part2(part1())
}
