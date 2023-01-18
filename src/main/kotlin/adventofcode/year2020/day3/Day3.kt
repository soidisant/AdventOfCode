package adventofcode.year2020.day3

import java.io.File

class Tobogan() {
    lateinit var topology: MutableList<List<Boolean>>

    constructor(file: File) : this() {
        initTopology(file)
    }

    fun initTopology(file: File) {
        topology = mutableListOf<List<Boolean>>()
        file.forEachLine {
            topology.add(it.toCharArray().map { it == '#' })
        }
    }

    fun printMap() {
        for (row in topology) {
            println(row.joinToString("") { if (it) "#" else "." })
        }
    }

    fun width() =
        topology.first().size

    fun height() = topology.size

    fun get(x: Int, y: Int): Boolean =
        topology[y][x % width()]

    fun slope(slideX: Int, slideY: Int): Long {
        val map: MutableMap<Pair<Int, Int>, Boolean> = mutableMapOf<Pair<Int, Int>, Boolean>()
        var position = Pair(0, 0)
        while (position.second < height() - 1) {
            position = Pair(position.first + slideX, position.second + slideY)
            map[position] = get(position.first, position.second)
        }
        return map.filterValues { it }.count().toLong()
    }
}

fun main() {
    val file = File(Thread.currentThread().contextClassLoader.getResource("2020/day3input.txt")!!.path)

    val tobogan = Tobogan(file)
    tobogan.printMap()

    val part2 = tobogan.slope(1, 1) *
        tobogan.slope(3, 1) *
        tobogan.slope(5, 1) *
        tobogan.slope(7, 1) *
        tobogan.slope(1, 2)
    println(part2)
}
