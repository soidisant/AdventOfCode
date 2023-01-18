package adventofcode.year2022.day18

import adventofcode.comon.Grid3d
import adventofcode.comon.Point3d
import adventofcode.comon.puzzleInputBufferedReader

class BoilingBoulders {

    private val grid = Grid3d<Boolean>()
    private val xRange: IntRange
    private val yRange: IntRange
    private val zRange: IntRange

    init {
        val reg = "(\\d+),(\\d+),(\\d+)".toRegex()
        puzzleInputBufferedReader(2022, "day18.txt").forEachLine { line ->
            reg.find(line)?.destructured?.let { (x, y, z) ->
                grid[x.toInt(), y.toInt(), z.toInt()] = true
            }
        }
        // -1 <-> +1 to account for faces of blocks touching the borders
        xRange = grid.minX - 1..grid.maxX + 1
        yRange = grid.minY - 1..grid.maxY + 1
        zRange = grid.minZ - 1..grid.maxZ + 1
    }

    fun surfaceArea(): Int {
        var sum = 0
        grid.forEach { x, y, z, _ ->
            sum += 6 - grid.orthogonalNeighbors(x, y, z).size
        }
        return sum
    }

    fun externalSurfaceArea(): Int {
        exploreGrid()
        var sum = 0
        grid.forEach { x, y, z, isCube ->
            if (isCube) {
                sum += grid.orthogonalNeighbors(x, y, z).count { !it.second }
            }
        }
        return sum
    }

    // start from a point outside the ranges, and explore the grid,
    // all reachable points which are not cubes are set to false
    // air pockets will we be all remaining points in ranges that are null
    private fun exploreGrid() {
        val points = mutableListOf(Point3d(xRange.last, yRange.last, zRange.last))
        while (points.isNotEmpty()) {
            val p = points.removeFirst()
            if (p.inGrid() && grid[p] == null) {
                grid[p] = false
                points.add(Point3d(p.x - 1, p.y, p.z))
                points.add(Point3d(p.x + 1, p.y, p.z))
                points.add(Point3d(p.x, p.y - 1, p.z))
                points.add(Point3d(p.x, p.y + 1, p.z))
                points.add(Point3d(p.x, p.y, p.z - 1))
                points.add(Point3d(p.x, p.y, p.z + 1))
            }
        }
    }

    private fun Point3d.inGrid(): Boolean =
        (x in xRange) && (y in yRange) && (z in zRange)
}

fun main() {
    val boilingBoulders = BoilingBoulders()

    val surfaceArea = boilingBoulders.surfaceArea()
    println("Part1: surface area is $surfaceArea")

    val externalSurfaceArea = boilingBoulders.externalSurfaceArea()
    println("Part2: surface area is $externalSurfaceArea")
}
