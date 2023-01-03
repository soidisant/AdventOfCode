package advent2022.day12

import comon.puzzleInputBufferedReader

class HillClimbingAlgorithm {
    data class Distance(var steps: Int, var towards: Direction)
    data class Point(val elevation: Char, var distance: Distance? = null)
    data class Coordinates(var x: Int, var y: Int) {
        fun set(x: Int, y: Int) {
            this.x = x
            this.y = y
        }

        fun move(direction: Direction) = when (direction) {
            Direction.UP -> y--
            Direction.DOWN -> y++
            Direction.LEFT -> x--
            Direction.RIGHT -> x++
            else -> {}
        }
    }

    enum class Direction(val print: String) { UP("^"), DOWN("v"), LEFT("<"), RIGHT(">"), STOP("") }

    val Direction.reverse
        get() = when (this) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.LEFT -> Direction.RIGHT
            Direction.RIGHT -> Direction.LEFT
            else -> this
        }

    var grid = mutableListOf<MutableList<Point>>()
    var start = Coordinates(0, 0)
    var destination = Coordinates(0, 0)
    val width by lazy {
        grid.first().size
    }
    val height by lazy {
        grid.size
    }

    val Coordinates.neighbors
        get() = Direction.values().filter {
            when (it) {
                Direction.UP -> y - 1 in 0 until height
                Direction.DOWN -> y + 1 in 0 until height
                Direction.LEFT -> x - 1 in 0 until width
                Direction.RIGHT -> x + 1 in 0 until width
                else -> false
            }
        }.associate {
            when (it) {
                Direction.UP -> Direction.UP to Coordinates(x, y - 1)
                Direction.DOWN -> Direction.DOWN to Coordinates(x, y + 1)
                Direction.LEFT -> Direction.LEFT to Coordinates(x - 1, y)
                Direction.RIGHT -> Direction.RIGHT to Coordinates(x + 1, y)
                else -> {
                    throw Exception("cannot happen")
                }
            }
        }

    init {
        puzzleInputBufferedReader(2022, "day12.txt").readLines().forEachIndexed { y, line ->
            grid.add(mutableListOf())
            line.forEachIndexed { x, elevation ->
                when (elevation) {
                    'S' -> {
                        start.set(x, y)
                        grid[y].add(Point('a'))
                    }
                    'E' -> {
                        destination.set(x, y)
                        grid[y].add(Point('z', Distance(0, Direction.DOWN)))
                    }
                    else -> grid[y].add(Point(elevation))
                }
            }
        }
        calculateDistances(destination)
    }

    fun printElevations() {
        grid.forEach { println(it.map { it.elevation }.joinToString()) }
    }

    fun printDistances() {
        grid.forEach { println(it.map { "${it.distance?.steps ?: "."}${it.distance?.towards?.print ?: ""}" }) }
    }

    fun printPath() {
        val path = pathToDestination()
        grid.forEachIndexed { y, line ->
            line.forEachIndexed { x, point ->
                if (x == destination.x && y == destination.y) {
                    print(" E ")
                } else if (x == start.x && y == start.y) {
                    print(" S" + point.distance!!.towards.print)
                } else {
                    if (path.contains(Coordinates(x, y))) {
                        print(" " + (point.distance?.towards?.print ?: ".") + " ")
                    } else {
                        print(" . ")
                    }
                }
            }
            println()
        }
    }

    fun pathToDestination(): MutableList<Coordinates> {
        val path = mutableListOf<Coordinates>()
        val coord = start.copy()
        do {
            path.add(coord.copy())
            coord.move(grid[coord.y][coord.x].distance!!.towards)
        } while ((coord != destination))
        return path
    }

    fun calculateDistances(startingPoint: Coordinates) {
        var coords = mutableListOf<Coordinates>(startingPoint)
        while (coords.isNotEmpty()) {
            val nextCoords = mutableListOf<Coordinates>()
            coords.forEach { point ->
                point.neighbors.forEach { (direction, neighbor) ->
                    val toElevation = grid[point.y][point.x].elevation
                    val fromElevation = grid[neighbor.y][neighbor.x].elevation
                    if ((
                        fromElevation >= toElevation ||
                            (toElevation - fromElevation) < 2
                        ) &&
                        neighbor.isPossibleShortestPathTo(point, direction.reverse)
                    ) {
                        nextCoords.add(neighbor)
                    }
                }
            }
            coords = nextCoords
        }
    }

    private fun Coordinates.isPossibleShortestPathTo(to: Coordinates, directionFromTo: Direction): Boolean {
        val from = this
        if (from == destination) {
            return false
        }
        val toDistance = grid[to.y][to.x].distance!!
        if (grid[from.y][from.x].distance == null) {
            grid[from.y][from.x].distance = Distance(toDistance.steps + 1, directionFromTo)
            return true
        }
        val fromDistance = grid[from.y][from.x].distance!!
        if ((toDistance.steps + 1) < fromDistance.steps) {
            grid[from.y][from.x].distance!!.steps = toDistance.steps + 1
            grid[from.y][from.x].distance!!.towards = directionFromTo
            return true
        }
        return false
    }
}

fun main() {
    val hill = HillClimbingAlgorithm()
//    hill.printElevations()
//    hill.printDistances()
    hill.printPath()

    println("Part 1 : S -> E takes ${hill.grid[hill.start.y][hill.start.x].distance!!.steps} steps ")

    val startsFromElevationA = mutableListOf<HillClimbingAlgorithm.Distance>()
    hill.grid.forEachIndexed { y, line ->
        line.forEachIndexed { x, point ->
            point.takeIf { it.elevation == 'a' }?.also {
                hill.grid[y][x].distance?.also {
                    startsFromElevationA.add(it)
                }
            }
        }
    }
    val minA = startsFromElevationA.minOf { it.steps }
    println("Part 2 : there are ${startsFromElevationA.size} possible paths starting from elevation a")
    println("The shortest takes $minA steps to reach E")
}
