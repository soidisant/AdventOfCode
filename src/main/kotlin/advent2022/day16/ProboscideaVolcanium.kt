package advent2022.day16

import comon.puzzleInputBufferedReader

class ProboscideaVolcanium {

    val valves = mutableMapOf<String, Valve>()

    init {
        val lineRegex =
            "Valve ([A-Z]{2}) has flow rate=(\\d+); tunnels? leads? to valves? ((?>[A-Z]{2},? ?)+)".toRegex()
        puzzleInputBufferedReader(2022, "day16.txt").forEachLine { line ->
            lineRegex.find(line)?.destructured?.let { (valveName, flowRate, tunnels) ->
                val valve = valves.getOrPut(valveName) {
                    Valve(valveName, flowRate.toInt(), tunnels.split(",").map(String::trim))
                }
            }
        }
    }

    val Valve.children
        get() = valves.filter { it.key in tunnels }.values

    fun Trajectory.openValve(valve: Valve) {
        opened.add(valve.name)
    }

    fun Trajectory.incrementReleasedPressure() {
        opened.forEach { valveName -> releasedPressure += valves[valveName]!!.flowRate }
    }

    fun CombinedTrajectory.incrementReleasedPressure() {
        opened.forEach { valveName -> releasedPressure += valves[valveName]!!.flowRate }
    }

    fun Trajectory.releaseRate() = opened.sumOf { valves[it]!!.flowRate }
    fun CombinedTrajectory.releaseRate() = opened.sumOf { valves[it]!!.flowRate }

    fun Trajectory.next(maxReleased: Int): Set<Trajectory> {
        val valve = valves[current]!!
        val trajectories = mutableSetOf<Trajectory>()
        incrementReleasedPressure()
        if (releasedPressure < maxReleased) {
            return emptySet()
        }
        if (opened.size < valves.size) {
            valve.children.forEach { child ->
                if (child.name != last || (!opened.contains(child.name) && child.name == last)) {
                    val newTrajectory = Trajectory(child.name, current, opened.toMutableSet(), releasedPressure)
                    trajectories.add(newTrajectory)
                }
            }
            if (valve.flowRate > 0 && !opened.contains(valve.name)) {
                openValve(valve)
                last = current
                trajectories.add(this)
            }
        } else {
            last = current
            trajectories.add(this)
        }
        return trajectories
    }

    fun findMaxReleasePressure() {
        var trajectories = setOf(
            Trajectory(
                "AA",
                "AA"
            )
        )
        val minutes = 30
        var minute = 1
        val maxReleaseRate = valves.values.sumOf { it.flowRate }

        repeat(minutes) {
            val remainingMinutes = minutes - minute
            val maxPotential = trajectories.maxOf { it.releasedPressure + (it.releaseRate() * remainingMinutes) }
            val maxReleased = if (minute > 10) {
                trajectories.maxOf { it.releasedPressure }
            } else 0
//            println("minute $minute/$minutes : ${trajectories.size} trajectories, maxReleased : $maxReleased")
            val tmp = mutableSetOf<Trajectory>()
            val remaining = maxReleaseRate * remainingMinutes
            trajectories.forEach { trajectory ->
                if (trajectory.releasedPressure + remaining >= maxPotential) {
                    tmp.addAll(trajectory.next(maxReleased))
                }
            }
            trajectories = tmp
            minute++
        }
        println("Part1: the most pressure I can release in $minutes minutes is ${trajectories.maxOf { it.releasedPressure }}")
    }

    fun CombinedTrajectory.next(maxReleased: Int): Set<CombinedTrajectory> {
        val trajectories = mutableSetOf<CombinedTrajectory>()
        incrementReleasedPressure()
        if (releasedPressure < maxReleased) {
            return emptySet()
        }
        if (opened.size < valves.size) {
            val myNextTrajectories =
                Trajectory(myPosition, myLastPosition, opened.toMutableSet(), releasedPressure).next(maxReleased)
            val elephantNextTrajectories =
                Trajectory(elephantPosition, elephantLastPosition, opened.toMutableSet(), releasedPressure).next(
                    maxReleased
                )
            myNextTrajectories.forEach { myNext ->
                elephantNextTrajectories
                    .forEach { elephantNext ->
                        trajectories.add(
                            CombinedTrajectory(
                                myNext.current,
                                elephantNext.current,
                                myNext.last,
                                elephantNext.last,
                                myNext.opened.toMutableSet().also { it.addAll(elephantNext.opened) },
                                releasedPressure
                            )
                        )
                    }
            }
        } else {
            myLastPosition = myPosition
            elephantLastPosition = elephantPosition
            trajectories.add(this)
        }
        return trajectories
    }

    fun findMaxReleasePressureWithAnElephant() {
        var trajectories = setOf(
            CombinedTrajectory(
                "AA",
                "AA",
                "AA",
                "AA"
            )
        )
        val minutes = 26
        var minute = 1
        val maxReleaseRate = valves.values.sumOf { it.flowRate }

        repeat(minutes) {
            val remainingMinutes = minutes - minute
            val maxPotential = trajectories.maxOf { it.releasedPressure + (it.releaseRate() * remainingMinutes) }
            val maxReleased = if (minute > 2) {
                trajectories.maxOf { it.releasedPressure }
            } else 0
            val tmp = mutableSetOf<CombinedTrajectory>()
            val remaining = maxReleaseRate * remainingMinutes
//            println("minute $minute/$minutes : ${trajectories.size} trajectories, maxReleased : $maxReleased")
            trajectories.forEach { trajectory ->
                if (trajectory.releasedPressure + remaining >= maxPotential) {
                    tmp.addAll(trajectory.next(maxReleased))
                }
            }
            trajectories = tmp
            minute++
        }
        println("Part2: the most pressure I can release in $minutes minutes is ${trajectories.maxOf { it.releasedPressure }}")
    }
}

data class Valve(val name: String, val flowRate: Int, val tunnels: List<String>)

data class Trajectory(
    val current: String,
    var last: String,
    val opened: MutableSet<String> = mutableSetOf(),
    var releasedPressure: Int = 0
)

data class CombinedTrajectory(
    val myPosition: String,
    val elephantPosition: String,
    var myLastPosition: String,
    var elephantLastPosition: String,
    val opened: MutableSet<String> = mutableSetOf(),
    var releasedPressure: Int = 0
)

fun part1() {
    val volcano = ProboscideaVolcanium()
    volcano.findMaxReleasePressure()
}

fun part2() {
    val volcano = ProboscideaVolcanium()
    volcano.findMaxReleasePressureWithAnElephant()
}

fun main() {
    part1()
    part2()
}
