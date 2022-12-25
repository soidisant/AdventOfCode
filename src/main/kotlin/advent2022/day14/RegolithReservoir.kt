package advent2022.day14

import comon.puzzleInputBufferedReader

data class Coord(val x: Int, val y: Int)

class RegolithReservoir(val sandSource: Coord, val bottomLess: Boolean) {
    enum class Element { Rock, Sand, Air }

    private val grid = mutableMapOf<Int, MutableMap<Int, Element>>()
    private val deepest: Int
    private val minX: Int
        get() = grid.keys.minOf { it }
    private val maxX: Int
        get() = grid.keys.maxOf { it }

    fun get(x: Int, y: Int) =
        if (bottomLess || y < (deepest + 2))
            grid[x]?.get(y) ?: Element.Air
        else
            Element.Rock

    fun put(coord: Coord, element: Element) {
        put(coord.x, coord.y, element)
    }

    fun put(x: Int, y: Int, element: Element) {
        grid.getOrPut(x) {
            mutableMapOf()
        }[y] = element
    }

    fun drawLine(start: Coord, end: Coord) {
        put(start, Element.Rock)
        val xProgression =
            if (start.x <= end.x)
                start.x..end.x
            else
                start.x.downTo(end.x)
        val yProgression =
            if (start.y <= end.y)
                start.y..end.y
            else
                start.y.downTo(end.y)
        for (x in xProgression)
            for (y in yProgression)
                put(x, y, Element.Rock)
    }

    init {
        val reg = "(?>(\\d+),(\\d+))".toRegex()
        puzzleInputBufferedReader(2022, "day14.txt").forEachLine { line ->
            reg.findAll(line).map { it.groupValues.let { (_, x, y) -> Coord(x.toInt(), y.toInt()) } }
                .windowed(2).forEach { (start, end) ->
                    drawLine(start, end)
                }
        }
        deepest = grid.values.map { it.keys }.maxOf { it.maxOf { it } }
    }

    fun print() {
        print(Coord(minX, 0), Coord(maxX, deepest))
    }

    fun print(topLeft: Coord, bottomRight: Coord) {
        for (y in topLeft.y..bottomRight.y) {
            for (x in topLeft.x..bottomRight.x) {
                when (get(x, y)) {
                    Element.Rock -> print("#")
                    Element.Sand -> print("o")
                    Element.Air -> print(".")
                }
            }
            println("")
        }
    }

    fun dropSand(x: Int, y: Int): Boolean {
        if (!bottomLess || y <= deepest) {
            return if (get(x, y + 1) == Element.Air) {
                dropSand(x, y + 1)
            } else if (get(x - 1, y + 1) == Element.Air) {
                dropSand(x - 1, y + 1)
            } else if (get(x + 1, y + 1) == Element.Air) {
                dropSand(x + 1, y + 1)
            } else {
                put(x, y, Element.Sand)
                true
            }
        }
        return false
    }

    fun calculateSandUnits(): Int {
        var units = 0
        while (dropSand(sandSource.x, sandSource.y)) {
            units++
        }
        return units
    }

    fun fillWithSand(): Int {
        var units = 0
        do {
            dropSand(sandSource.x, sandSource.y)
            units++
        } while (get(sandSource.x, sandSource.y) != Element.Sand)
        return units
    }
}

fun part1() {
    val regolithReservoir = RegolithReservoir(Coord(500, 0), true)
    println("part1: ${regolithReservoir.calculateSandUnits()} units of sand come to rest before sand starts flowing into the abyss below")
//    regolithReservoir.print()
}

fun part2() {
    val regolithReservoir = RegolithReservoir(Coord(500, 0), false)
    println("part2: ${regolithReservoir.fillWithSand()} units of sand come to rest")
}

fun main() {
    part1()
    part2()
}