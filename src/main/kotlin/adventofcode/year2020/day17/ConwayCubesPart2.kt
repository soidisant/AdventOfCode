package adventofcode.year2020.day17

import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

data class Coordinates(val x: Int, val y: Int, val z: Int, val w: Int)

class PocketDimension4D {

    private val grid = mutableMapOf<Int, MutableMap<Int, MutableMap<Int, MutableMap<Int, Boolean>>>>()

    init {
        File(ClassLoader.getSystemResource("2020/day17input.txt").file).let { file ->
            var y = 0
            file.forEachLine { line ->
                var x = 0
                line.forEach {
                    put(x, y, 0, 0, it == '#')
                    x++
                }
                y++
            }
        }
    }

    fun get(x: Int, y: Int, z: Int, w: Int) = grid[x]?.get(y)?.get(z)?.get(w)

    fun get(coordinates: Coordinates) = get(coordinates.x, coordinates.y, coordinates.z, coordinates.w)

    private fun put(x: Int, y: Int, z: Int, w: Int, active: Boolean) {
        grid.getOrPut(x) {
            mutableMapOf()
        }.getOrPut(y) {
            mutableMapOf()
        }.getOrPut(z) {
            mutableMapOf()
        }[w] = active
    }

    private fun put(coordinates: Coordinates, active: Boolean) =
        put(coordinates.x, coordinates.y, coordinates.z, coordinates.w, active)

    private inline fun forEachCube(action: (Int, Int, Int, Int, Boolean) -> Unit) {
        for ((x, xDimension) in grid) {
            for ((y, yDimension) in xDimension) {
                for ((z, zDimension) in yDimension) {
                    for ((w, active) in zDimension) {
                        action(x, y, z, w, active)
                    }
                }
            }
        }
    }

    private inline fun forEachNeighborsOf(coordinates: Coordinates, action: (Coordinates) -> Unit) =
        forEachNeighborsOf(coordinates.x, coordinates.y, coordinates.z, coordinates.w, action)

    private inline fun forEachNeighborsOf(x: Int, y: Int, z: Int, w: Int, action: (Coordinates) -> Unit) {
        for (nx in x - 1..x + 1) {
            for (ny in y - 1..y + 1)
                for (nz in z - 1..z + 1) {
                    for (nw in w - 1..w + 1) {
                        val coordinate = Coordinates(nx, ny, nz, nw)
                        if (coordinate != Coordinates(x, y, z, w)) {
                            action(coordinate)
                        }
                    }
                }
        }
    }

    private fun countActive(): Int {
        var count = 0
        grid.forEach { (_, xDimension) ->
            xDimension.forEach { (_, yDimension) ->
                yDimension.forEach { (_, zDimension) ->
                    zDimension.forEach { (_, active) ->
                        if (active) {
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    private fun hasActiveNeighbors(coordinates: Coordinates): Boolean {
        forEachNeighborsOf(coordinates) {
            if (get(it) == true) {
                return true
            }
        }
        return false
    }

    private fun addNeighbors(coordinates: Coordinates) {
        val newCubes = mutableSetOf<Coordinates>()

        forEachNeighborsOf(coordinates.x, coordinates.y, coordinates.z, coordinates.w) { neighbor ->
            if (get(neighbor) == null) {
                newCubes.add(neighbor)
            }
        }
        newCubes.forEach {
            put(it, false)
        }
    }

    private fun initNeighbors() {
        // before 1st cycle, we need to add all neighbors cubes to active ones
        val newCubes = mutableSetOf<Coordinates>()
        forEachCube { x, y, z, w, active ->
            if (active) {
                forEachNeighborsOf(x, y, z, w) { coordinates ->
                    if (get(coordinates) == null) {
                        newCubes.add(coordinates)
                    }
                }
            }
        }
        newCubes.forEach {
            put(it, false)
        }
    }

    private fun cycle() {
        // cubes that will change state
        val changes = mutableSetOf<Coordinates>()
        // inactive cubes with potentially no active neighbors
        val removes = mutableSetOf<Coordinates>()
        forEachCube { x, y, z, w, active ->
            var activeNeighbors = 0
            forEachNeighborsOf(x, y, z, w) { coordinates ->
                val neighbor = get(coordinates)
                if (neighbor != null && neighbor) {
                    activeNeighbors++
                    removes.remove(coordinates)
                }
            }
            if ((active && (activeNeighbors != 2 && activeNeighbors != 3)) || (!active && activeNeighbors == 3)) {
                changes.add(Coordinates(x, y, z, w))
            } else if (!active && activeNeighbors == 0) {
                removes.add(Coordinates(x, y, z, w))
            }
        }
        changes.forEach {
            val state = !(get(it) ?: true)
            put(it, state)
            // a cube becomes active, making sur its neighbors are in the grid
            if (state) {
                addNeighbors(it)
            }
        }

        removes.forEach {
            if (!hasActiveNeighbors(it) && !get(it)!!) {
                grid[it.x]?.get(it.y)?.get(it.z)?.remove(it.w)
            }
        }
    }

    fun cycles(cycles: Int) {
        initNeighbors()
        println("there is ${countActive()} active cubes")
        for (i in 1..cycles) {
            cycle()
            println("there is ${countActive()} active cubes after $i cycles")
        }
    }
}

@OptIn(ExperimentalTime::class)
fun main() {
    val time = measureTime {
        PocketDimension4D().cycles(6)
    }
    println("it took $time")
}
