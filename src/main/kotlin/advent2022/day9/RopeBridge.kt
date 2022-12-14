package advent2022.day9

import comon.puzzleInputBufferedReader
import kotlin.math.absoluteValue
import kotlin.math.sign

enum class Direction(val short: String) {
    UP("U"), DOWN("D"), LEFT("L"), RIGHT("R"), SAME("S")
}

data class Knot(var x: Int = 0, var y: Int = 0) {
    val position
        get() = Pair(x, y)

    fun adjacentTo(knot: Knot) = (x - knot.x).absoluteValue < 2 && (y - knot.y).absoluteValue < 2
    fun follow(knot: Knot) {
        directionOf(knot).let { (horizontal, vertical) ->
            move(horizontal)
            move(vertical)
        }
    }

    fun directionOf(knot: Knot) = Pair(
        when (knot.x.compareTo(x).sign) {
            1 -> Direction.RIGHT
            0 -> Direction.SAME
            else -> Direction.LEFT
        }, when (knot.y.compareTo(y).sign) {
            1 -> Direction.UP
            0 -> Direction.SAME
            else -> Direction.DOWN
        }
    )

    fun move(direction: Direction) {
        when (direction) {
            Direction.UP -> y++
            Direction.DOWN -> y--
            Direction.LEFT -> x--
            Direction.RIGHT -> x++
            Direction.SAME -> {}
        }
    }
}

class Rope(length: Int) {
    val knots: List<Knot>
    val head: Knot
    val tail: Knot

    init {
        knots = List(length) { Knot() }
        head = knots.first()
        tail = knots.last()
    }

    fun moveHead(direction: Direction) {
        head.move(direction)
        knots.windowed(2).forEach { (currentHead, currentTail) ->
            if (!currentTail.adjacentTo(currentHead)) {
                currentTail.follow(currentHead)
            }
        }
    }
}

fun simulate(ropeLength: Int) {
    val rope = Rope(ropeLength)
    val regex = "\\s".toRegex()
    val tailPositions = mutableSetOf<Pair<Int, Int>>(Pair(0, 0))
    puzzleInputBufferedReader(2022, "day9.txt").forEachLine { line ->
        line.split(regex, 2).let { (d, s) ->
            val direction = Direction.values().first { it.short == d }
            val steps = s.toInt()
            repeat(steps) {
                rope.moveHead(direction)
                tailPositions.add(rope.tail.position)
            }

        }
    }
    println("With a rope composed of $ropeLength knots, the tail visited ${tailPositions.size} places")
}

fun main() {
    simulate(2)
    simulate(10)
}
