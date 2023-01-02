package advent2022.day15

import comon.GridByRows
import comon.Point
import comon.puzzleInputBufferedReader
import kotlin.math.absoluteValue

fun Point.taxicabDistance(other: Point) = (x - other.x).absoluteValue + (y - other.y).absoluteValue
val Point.tuningFrequency
    get() = (x.toLong() * 4000000L) + y.toLong()

class BeaconExclusionZone {
    enum class Element { Beacon, Sensor, Empty }

    val grid = GridByRows<Element>()
    val sensors = mutableListOf<Pair<Point.Fixed, Int>>()

    init {
        val reg = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
        puzzleInputBufferedReader(2022, "day15.txt").forEachLine { line ->
            reg.find(line)?.groupValues?.let { (_, sensorX, sensorY, beaconX, beaconY) ->
                val sensor = Point.Fixed(sensorX.toInt(), sensorY.toInt())
                val beacon = Point.Fixed(beaconX.toInt(), beaconY.toInt())
                grid.put(sensor, Element.Sensor)
                grid.put(beacon, Element.Beacon)
                sensors.add(sensor to sensor.taxicabDistance(beacon))
            }
        }
    }

    fun setNoBeaconAtRow(row: Int, sensor: Point.Fixed, closestBeacon: Int) {
        for (x in 0..closestBeacon) {
            val yProgression = (sensor.y - (closestBeacon - x))..(sensor.y + closestBeacon - x)
            if (row in yProgression) {
                if (grid[sensor.x + x, row] == null) {
                    grid.put(sensor.x + x, row, Element.Empty)
                }
                if (grid[sensor.x - x, row] == null) {
                    grid.put(sensor.x - x, row, Element.Empty)
                }
            }
        }
    }

    fun Point.pointsAt(taxiCabDistance: Int): Sequence<Point.Fixed> {
        var x = this.x - taxiCabDistance
        return sequence {
            generateSequence {
                if (x <= this@pointsAt.x + taxiCabDistance) {
                    taxiCabDistance - (x - this@pointsAt.x).absoluteValue
                } else {
                    null
                }
            }.forEach { y ->
                yield(Point.Fixed(x, this@pointsAt.y + y))
                if (y != 0) {
                    yield(Point.Fixed(x, this@pointsAt.y - y))
                }
                x++
            }
        }
    }

    inline fun isDistressBeacon(point: Point) =
        !sensors.any { (sensor, taxicabDistance) ->
            point.taxicabDistance(sensor) <= taxicabDistance
        }

    fun findDistressBeacon(topLeftBound: Point, bottomRightBound: Point): Point {
        sensors.forEach { (sensor, closestBeacon) ->
            println("testing sensor $sensor closest beacon at $closestBeacon units")
            sensor.pointsAt(closestBeacon + 1).forEach { point ->
                if (point.x in topLeftBound.x..bottomRightBound.x &&
                    point.y in topLeftBound.y..bottomRightBound.y &&
                    isDistressBeacon(point)
                ) {
                    return point
                }
            }
        }
        throw Exception("Distress beacon could not be found!")
    }
}

fun main() {
    val beaconExclusionZone = BeaconExclusionZone()
    val row = 2000000
    beaconExclusionZone.sensors.forEach { (sensor, beaconDistance) ->
        beaconExclusionZone.setNoBeaconAtRow(row, sensor, beaconDistance)
    }
    val total = beaconExclusionZone.grid.row(row)!!.values.count {
        it != BeaconExclusionZone.Element.Beacon
    }
    println("Part1: in the row where y=$row, there are $total positions where a beacon cannot be present.")

    val beacon = beaconExclusionZone.findDistressBeacon(Point.Fixed(0, 0), Point.Fixed(4000000, 4000000))
    println("Part2: Beacon is at $beacon, it's tuning frequency is ${beacon.tuningFrequency}")
}
