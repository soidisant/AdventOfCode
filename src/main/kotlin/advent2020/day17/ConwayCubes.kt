package advent2020.day17

import java.io.File

val pocketDimension: MutableSet<ConwayCubes> by lazy {
    mutableSetOf<ConwayCubes>().also { set ->
        File(ClassLoader.getSystemResource("2020/day17input.txt").file).let { file ->
            var y = 0
            file.forEachLine { line ->
                var x = 0
                line.forEach {
                    set.add(ConwayCubes(Triple(x++, y, 0), it == '#'))
                }
                y++
            }
        }
    }
}

data class ConwayCubes(val coord: Triple<Int, Int, Int>, var active: Boolean) {

    val x = coord.first
    val y = coord.second
    val z = coord.third

    fun neighborsCoordinates() = mutableSetOf<Triple<Int, Int, Int>>().also { set ->
        for (nx in x - 1..x + 1) {
            for (ny in y - 1..y + 1)
                for (nz in z - 1..z + 1) {
                    val triple = Triple(nx, ny, nz)
                    if (triple != coord) {
                        set.add(triple)
                    }
                }
        }
    }

    fun symbol(): String {
        return if (active)
            "#"
        else
            "."
    }
}

fun printPocketDimension() {
    val zMax = pocketDimension.maxOf { it.z }
    val zMin = pocketDimension.minOf { it.z }
    // val xMin = pocketDimension.minOf { it.x }
    // val xMax = pocketDimension.maxOf { it.x }
    val yMin = pocketDimension.minOf { it.y }
    val yMax = pocketDimension.maxOf { it.y }
    for (z in zMin..zMax) {
        println("z=$z")
        for (y in yMin..yMax) {
            pocketDimension.filter { it.y == y && it.z == z }.sortedBy { it.x }.forEach {
                print(it.symbol())
            }
            println()
        }
    }
}

fun cycle() {
    val newCubes = mutableSetOf<ConwayCubes>()
    pocketDimension.filter { it.active }.forEach { cube ->
        val neighborsCoords = cube.neighborsCoordinates()
        neighborsCoords.forEach { coord ->
            if (pocketDimension.find { it.coord == coord } == null) {
                newCubes.add(ConwayCubes(coord, false))
            }
        }
    }
    pocketDimension.addAll(newCubes)
    val changes = mutableSetOf<ConwayCubes>()
    pocketDimension.forEach { cube ->
        val neighborsCoords = cube.neighborsCoordinates()
        pocketDimension.filter { it.coord in neighborsCoords }.let { neighbors ->
            val activeNeighbors = neighbors.count { it.active }
            if (cube.active && (activeNeighbors != 2 && activeNeighbors != 3)) {
                changes.add(cube)
            } else if (!cube.active && activeNeighbors == 3) {
                changes.add(cube)
            }
        }
    }
    changes.forEach {
        it.active = !it.active
    }
}

fun cycles(cycles: Int) {

    println("Before any cycles: ")
    printPocketDimension()

    for (i in 1..cycles) {
        cycle()
        println("After $i cycles:")
//        printPocketDimension()
    }
}

fun main() {

//    val cube = ConwayCubes(Triple(0, 0, 0), false)

    // println(cube.neighborsCoordinates().size)
    println(pocketDimension)
    cycles(6)
    println("part 1 there is ${pocketDimension.count { it.active }} actives cubes after 6 cycles")
}
