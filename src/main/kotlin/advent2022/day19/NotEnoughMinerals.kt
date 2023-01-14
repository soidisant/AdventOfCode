package advent2022.day19

import comon.puzzleInputBufferedReader
import comon.timeIt

fun simulate(minutes: Int, bluePrints: List<BluePrint>) {
    val qualities = mutableMapOf<Int, Pair<Int, Int>>()
    bluePrints.forEach { bluePrint ->
        val start = Possibility(1, 0, 0, 0)
        var possibilities: Map<String, Possibility> = mapOf("first" to start)
//        println("blueprint ${bluePrint.id}")
        for (minute in 1..minutes) {
            val newPossibilities = mutableMapOf<String, Possibility>()
            val maxGeodes = possibilities.maxOfOrNull { it.value.geodes } ?: 0
//            println("blueprint ${bluePrint.id} min $minute| $maxGeodes | ${possibilities.size} ")
            possibilities.forEach {
                val poss = it.value
//                if (poss.geodes >= maxGeodes - 2) {
//                println((minutes-minute))
                if (poss.geodes + poss.geodeRobot + 1 >= maxGeodes) {
                    val newRobots = if (bluePrint.canBuildGeodeRobot(poss)) {
                        listOf(
                            false,
                            false,
                            false,
                            true
                        )
                    } else {
                        listOf(
                            bluePrint.canBuildOreRobot(poss) && poss.oreRobot < bluePrint.maxOreNeeded,
                            bluePrint.canBuildClayRobot(poss) && poss.clayRobot < bluePrint.maxClayNeeded,
                            bluePrint.canBuildObsidianRobot(poss) && poss.obsidianRobot < bluePrint.maxObsidianNeeded,
                            false
                        )
                    }

                    poss.produce()

                    newRobots.also { (newOre, newClay, newObs, newGeode) ->
                        if (newGeode) {
                            poss.copy(
                                ore = poss.ore - bluePrint.geodeRobotCost.first,
                                obsidians = (poss.obsidians - bluePrint.geodeRobotCost.second),
                                geodeRobot = (poss.geodeRobot + 1)
                            ).also {
                                if (minute == 26 && poss.oreRobot == 7 && poss.clayRobot == 2 && poss.obsidianRobot == 5) {
                                    println("$minute $it")
                                    readln()
                                }
                                addToNewPoss(newPossibilities, it)
                            }
                        }
                        if (newObs) {
                            poss.copy(
                                ore = (poss.ore - bluePrint.obsidianRobotCost.first),
                                clay = (poss.clay - bluePrint.obsidianRobotCost.second),
                                obsidianRobot = (poss.obsidianRobot + 1)
                            ).also {
                                addToNewPoss(newPossibilities, it)
                            }
                        }
                        if (newClay) {
                            poss.copy(
                                ore = (poss.ore - bluePrint.clayRobotCost),
                                clayRobot = (poss.clayRobot + 1)
                            ).also {
                                addToNewPoss(newPossibilities, it)
                            }
                        }
                        if (newOre) {
                            poss.copy(
                                ore = (poss.ore - bluePrint.oreRobotCost),
                                oreRobot = (poss.oreRobot + 1)
                            ).also {
                                addToNewPoss(newPossibilities, it)
                            }
                        }
                        addToNewPoss(newPossibilities, poss.copy())
                    }
                }
            }
            possibilities = newPossibilities
        }

        var maxQuality = possibilities.maxOf { bluePrint.id * it.value.geodes }
        var maxGeodes = possibilities.maxOf { it.value.geodes }
        qualities[bluePrint.id] = maxGeodes to maxQuality
//        println(qualities[bluePrint.id])
    }
    println("bluePrints id=(maxGeodes,quality) | $qualities")
    println(
        "quality = " + qualities.values.sumOf { it.second.toLong() }
    )
    println(
        "geodes* = " + qualities.values.fold(1L) { acc, pair -> acc * pair.first.toLong() }
    )
}

fun addToNewPoss(newPossibilities: MutableMap<String, Possibility>, possibility: Possibility) {
    val existing = newPossibilities[possibility.robotsId]
    if (existing == null) {
        if (newPossibilities[possibility.id] == null) {
            newPossibilities[possibility.robotsId] = possibility
        }
    } else {
        if (existing.oreId == possibility.oreId) {
            if (existing.ore < possibility.ore) {
                existing.ore = possibility.ore
                return
            }
        }
        if (existing.clayId == possibility.clayId) {
            if (existing.clay < possibility.clay) {
                existing.clay = possibility.clay
                return
            }
        }
        if (existing.obsidianId == possibility.obsidianId) {
            if (existing.obsidians < possibility.obsidians) {
                existing.obsidians = possibility.obsidians
                return
            }
        }
        if (existing.geodeId == possibility.geodeId) {
            if (existing.geodes < possibility.geodes) {
                existing.geodes = possibility.geodes
                return
            }
        }
        newPossibilities[possibility.id] = possibility
    }
}

data class Possibility(
    var oreRobot: Int = 0,
    var clayRobot: Int = 0,
    var obsidianRobot: Int = 0,
    var geodeRobot: Int = 0,
    var ore: Int = 0,
    var clay: Int = 0,
    var obsidians: Int = 0,
    var geodes: Int = 0
) {

    val id = "$robotsId|$resourceId"

    val robotsId: String
        get() = "$oreRobot|$clayRobot|$obsidianRobot|$geodeRobot"

    val resourceId
        get() = "$ore|$clay|$obsidians|$geodes"

    val oreId
        get() = "$clay|$obsidians|$geodes"

    val clayId
        get() = "$ore|$obsidians|$geodes"

    val obsidianId
        get() = "$ore|$clay|$geodes"

    val geodeId
        get() = "$ore|$clay|$obsidians"

    val value
        get() = geodes

    fun produce() {
        ore += oreRobot
        clay += clayRobot
        obsidians += obsidianRobot
        geodes += geodeRobot
    }
}

data class BluePrint(
    val id: Int,
    val oreRobotCost: Int,
    val clayRobotCost: Int,
    val obsidianRobotCost: Pair<Int, Int>,
    val geodeRobotCost: Pair<Int, Int>
) {
    val maxOreNeeded: Int = listOf(oreRobotCost, clayRobotCost, obsidianRobotCost.first, geodeRobotCost.first).max()
    val maxClayNeeded: Int = obsidianRobotCost.second
    val maxObsidianNeeded: Int = geodeRobotCost.second

    companion object {
        val all: MutableList<BluePrint> = mutableListOf()
        val test: MutableList<BluePrint> = mutableListOf(
            BluePrint(1, 4, 2, 3 to 14, 2 to 7),
            BluePrint(2, 2, 3, 3 to 8, 3 to 12)
        )

        init {
            val reg =
                "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.".toRegex()
            puzzleInputBufferedReader(2022, "day19.txt").forEachLine { line ->
                reg.find(line)?.destructured?.let { (id, oreRobotCost, clayRobotCost, obsidianRobotOre, obsidianRobotClay, geodeRobotOre, geodeRobotObsidian) ->
                    all.add(
                        BluePrint(
                            id.toInt(),
                            oreRobotCost.toInt(),
                            clayRobotCost.toInt(),
                            obsidianRobotOre.toInt() to obsidianRobotClay.toInt(),
                            geodeRobotOre.toInt() to geodeRobotObsidian.toInt()
                        )
                    )
                }
            }
        }
    }

    fun canBuildOreRobot(possibility: Possibility) = possibility.ore >= oreRobotCost
    fun canBuildClayRobot(possibility: Possibility) = possibility.ore >= clayRobotCost
    fun canBuildObsidianRobot(possibility: Possibility) =
        possibility.ore >= obsidianRobotCost.first && possibility.clay >= obsidianRobotCost.second

    fun canBuildGeodeRobot(possibility: Possibility) =
        possibility.ore >= geodeRobotCost.first && possibility.obsidians >= geodeRobotCost.second
}

fun main() {
    println("part1:")
//    timeIt {
//        simulate(24, BluePrint.all)
//    }
    println("part2:")
    timeIt {
        simulate(32, BluePrint.test)
//        simulate(32, BluePrint.all.subList(0, 3))
    }
}
