package adventofcode.year2022.day17

import adventofcode.comon.Grid
import adventofcode.comon.GridByRows
import adventofcode.comon.Point
import adventofcode.comon.puzzleInputBufferedReader
import adventofcode.comon.timeIt

class PyroclasticFlow(
    val leftBound: Int = 0,
    val rightBound: Int = 6
) {
    private var grid = GridByRows<Boolean>()

    private data class State(val fallenRocks: Long, val height: Long)

    private var states = mutableMapOf<String, State>()

    init {
        reset()
    }

    private fun reset() {
        grid.clear()
        // draw the floor
        for (x in leftBound..rightBound)
            grid[x, 0] = true
    }

    private fun Grid<Boolean>.putRock(rocks: Rocks) {
        rocks.points.forEach { point ->
            this[point.x, point.y] = true
        }
    }

    private fun Rocks.pushLeft() {
        if (leftMost.x > leftBound && points.all { grid[it.x - 1, it.y] == null }) {
            points.forEach { it.x-- }
        }
    }

    private fun Rocks.pushRight() {
        if (rightMost.x < rightBound && points.all { grid[it.x + 1, it.y] == null }) {
            points.forEach { it.x++ }
        }
    }

    private fun Rocks.fall(): Boolean {
        return if (points.all { grid[it.x, it.y - 1] == null }) {
            points.forEach { it.y -= 1 }
            true
        } else {
            false
        }
    }

    fun simulate(nbRocks: Long) {
        Rocks.resetIterator()
        JetIterator.reset()
        reset()
        states.clear()
        var currentHeight = 0L
        var highestRock = 0
        var fallenRocks = 1L

        while (fallenRocks <= nbRocks) {
            val rock = Rocks.iterator.next()
            // setting the next rock at correct height
            rock.raise(grid.maxY + 4)

            do {
                val jet = JetIterator.next()
                if (jet == '<') {
                    rock.pushLeft()
                } else {
                    rock.pushRight()
                }
            } while (rock.fall())
            grid.putRock(rock)

            if (grid.maxY > highestRock) {
                currentHeight += grid.maxY - highestRock
                highestRock = grid.maxY
            }

            // adjusting the floor if possible
            checkForNewFloor()?.let { newFloorY ->
                raiseFloor(newFloorY)
                highestRock = grid.maxY
            }

            // unique id of this 'tower'
            val id = "${JetIterator.index()}${rock.name()}${towerString()}"
            if (states[id] == null) {
                states[id] = State(fallenRocks, currentHeight)
            } else {
                // we have a cycle !
                val rocksAtBeginningOfCycle = states[id]!!.fallenRocks
                val heightAtBeginningOfCycle = states[id]!!.height
                val heightGained = currentHeight - heightAtBeginningOfCycle
                val rocksFallenDuringCycle = fallenRocks - rocksAtBeginningOfCycle
                val rocksLeft = nbRocks - fallenRocks
                currentHeight += heightGained * (rocksLeft / rocksFallenDuringCycle)
                val remaining = (rocksLeft % rocksFallenDuringCycle)
                val nextState = states.entries.first { it.value.fallenRocks == (rocksAtBeginningOfCycle + remaining) }
                currentHeight += nextState.value.height - heightAtBeginningOfCycle
                grid.clear()
                break
            }
            fallenRocks++
        }
        println("the tower of rocks will be $currentHeight units tall after $nbRocks rocks have stopped falling")
    }

    // a row can become the new floor it has a rock above at max distance 4 all along
    // 4 -> the pipe rock is 4 units tall, it's the only rock that could slip in a 1 unit hole
    private fun checkForNewFloor(): Int? {
        for (y in (1..grid.maxY).reversed()) {
            if ((leftBound..rightBound).all { x -> rockAbove(Point.Fixed(x, y), 4) }) {
                return y
            }
        }
        return null
    }

    private fun rockAbove(point: Point, atMaxDistance: Int): Boolean {
        for (y in point.y + 1..point.y + atMaxDistance) {
            if (grid[point.x, y] == true) {
                return true
            }
        }
        return false
    }

    private fun raiseFloor(newFloor: Int) {
        val newGrid = GridByRows<Boolean>()
        val maxY = grid.maxY
        for (y in newFloor..maxY) {
            newGrid.elements[y - newFloor] = grid.elements[y]!!
        }
        grid = newGrid
    }

    fun draw(rock: Rocks? = null) {
        val mY = rock?.points?.maxOf { it.y } ?: grid.maxY
        for (y in (1..mY).reversed()) {
            print("$y|")
            for (x in leftBound..rightBound) {
                if (rock != null && rock.points.contains(Point.Mutable(x, y))) {
                    print("@")
                } else {
                    if (grid[x, y] == null) print(".") else print("#")
                }
            }
            println("|")
        }
        println("0+-------+")
    }

    private fun towerString() =
        grid.elements.map { y -> "[${y.key}]" + y.value.map { x -> "${x.key}" }.joinToString("") }
            .joinToString("")
}

sealed class Rocks {
    abstract val points: List<Point.Mutable>
    abstract fun name(): Char
    abstract val leftMost: Point
    abstract val rightMost: Point

    fun raise(height: Int) {
        points.forEach {
            it.y += height
        }
    }

    class Minus : Rocks() {
        override val leftMost: Point
        override val rightMost: Point
        override val points: List<Point.Mutable>

        init {
            leftMost = Point.Mutable(2, 0)
            rightMost = Point.Mutable(5, 0)
            points = listOf(
                leftMost,
                Point.Mutable(3, 0),
                Point.Mutable(4, 0),
                rightMost
            )
        }

        override fun name(): Char = '-'
    }

    class Plus : Rocks() {
        override fun name(): Char = '+'
        override val leftMost: Point
        override val rightMost: Point
        override val points: List<Point.Mutable>

        init {
            leftMost = Point.Mutable(2, 1)
            rightMost = Point.Mutable(4, 1)
            points =
                listOf(
                    leftMost,
                    Point.Mutable(3, 1),
                    rightMost,
                    Point.Mutable(3, 0),
                    Point.Mutable(3, 2)
                )
        }
    }

    class ReverseL : Rocks() {
        override fun name(): Char = '⅃'
        override val leftMost: Point
        override val rightMost: Point
        override val points: List<Point.Mutable>

        init {
            leftMost = Point.Mutable(2, 0)
            rightMost = Point.Mutable(4, 0)
            points = listOf(
                leftMost,
                Point.Mutable(3, 0),
                rightMost,
                Point.Mutable(4, 1),
                Point.Mutable(4, 2)
            )
        }
    }

    class Pipe : Rocks() {
        override fun name(): Char = '|'
        override val leftMost: Point
        override val rightMost: Point
        override val points: List<Point.Mutable>

        init {
            leftMost = Point.Mutable(2, 0)
            rightMost = Point.Mutable(2, 1)
            points =
                listOf(
                    leftMost,
                    rightMost,
                    Point.Mutable(2, 2),
                    Point.Mutable(2, 3)
                )
        }
    }

    class Square : Rocks() {
        override fun name(): Char = '□'
        override val leftMost: Point
        override val rightMost: Point
        override val points: List<Point.Mutable>

        init {
            leftMost = Point.Mutable(2, 0)
            rightMost = Point.Mutable(3, 0)
            points =
                listOf(
                    leftMost,
                    rightMost,
                    Point.Mutable(2, 1),
                    Point.Mutable(3, 1)
                )
        }
    }

    companion object {
        protected var current: Rocks = Square()
        val iterator = object : Iterator<Rocks> {
            override fun hasNext() = true

            override fun next(): Rocks {
                current = when (current) {
                    is Minus -> Plus()
                    is Plus -> ReverseL()
                    is ReverseL -> Pipe()
                    is Pipe -> Square()
                    is Square -> Minus()
                }
                return current
            }
        }

        fun resetIterator() {
            current = Square()
        }

        fun next() {
            iterator.next()
        }
    }
}

object JetIterator : Iterator<Char> {
    private val jets = puzzleInputBufferedReader(2022, "day17.txt").readLine()
    var iterator = jets.iterator()
    private var index = 0
    override fun next(): Char {
        if (!iterator.hasNext()) {
            reset()
        }
        index++
        return iterator.next()
    }

    override fun hasNext(): Boolean = true

    fun index() = index

    fun reset() {
        iterator = jets.iterator()
        index = 0
    }
}

fun main() {
    val pyroclasticFlow = PyroclasticFlow()
    pyroclasticFlow.simulate(2022)
    timeIt { pyroclasticFlow.simulate(1000000000000) }
}
