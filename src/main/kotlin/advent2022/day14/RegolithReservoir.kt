package advent2022.day14

import comon.GridByColumns
import comon.Point
import comon.puzzleInputBufferedReader

class RegolithReservoir(val sandSource: Point, val bottomLess: Boolean) {
    enum class Element { Rock, Sand, Air }

    private val grid = GridByColumns<Element>()
    private val deepest: Int

    fun get(x: Int, y: Int) =
        if (bottomLess || y < (deepest + 2))
            grid[x, y] ?: Element.Air
        else
            Element.Rock

    fun drawLine(start: Point, end: Point) {
        grid.put(start, Element.Rock)
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
                grid.put(x, y, Element.Rock)
    }

    init {
        val reg = "(?>(\\d+),(\\d+))".toRegex()
        puzzleInputBufferedReader(2022, "day14.txt").forEachLine { line ->
            reg.findAll(line).map { it.groupValues.let { (_, x, y) -> Point(x.toInt(), y.toInt()) } }
                .windowed(2).forEach { (start, end) ->
                    drawLine(start, end)
                }
        }
        deepest = grid.maxY
    }

    fun print() {
        grid.println(Point(grid.minX, 0), Point(grid.maxX, grid.maxY)) { element ->
            when (element) {
                Element.Rock -> "#"
                Element.Sand -> "o"
                Element.Air -> "."
                null -> "."
            }
        }
    }


    fun dropSand(x: Int, y: Int): Boolean {
        if (!bottomLess || y <= deepest) {
            return when (Element.Air) {
                get(x, y + 1) -> {
                    dropSand(x, y + 1)
                }
                get(x - 1, y + 1) -> {
                    dropSand(x - 1, y + 1)
                }
                get(x + 1, y + 1) -> {
                    dropSand(x + 1, y + 1)
                }
                else -> {
                    grid.put(x, y, Element.Sand)
                    true
                }
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
    val regolithReservoir = RegolithReservoir(Point(500, 0), true)
    println("part1: ${regolithReservoir.calculateSandUnits()} units of sand come to rest before sand starts flowing into the abyss below")
    regolithReservoir.print()
}

fun part2() {
    val regolithReservoir = RegolithReservoir(Point(500, 0), false)
    println("part2: ${regolithReservoir.fillWithSand()} units of sand come to rest")
}

fun main() {
    part1()
    part2()
}