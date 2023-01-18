package adventofcode.year2022.day05

import adventofcode.comon.puzzleInputFile


open class SupplyStacks(val crane: Crane) {
    companion object {
        val stackRegex = "\\[([A-Z])]".toRegex()
        val endStacksRegex = "^(\\s+\\d+\\s*)+$".toRegex()
        val instructionRegex = "move (\\d+) from (\\d+) to (\\d+)".toRegex()

        private enum class ParseState {
            COMPUTE_STACKS,
            FILL_STACKS,
            READ_INSTRUCTION
        }
    }

    private var state = ParseState.COMPUTE_STACKS

    lateinit var stacks: List<ArrayDeque<Char>>

    fun parseInputFile() {
        puzzleInputFile(2022, "day5.txt").forEachLine {
            parseLine(it)
        }
    }

    private fun parseLine(line: String) {
        when (state) {
            ParseState.COMPUTE_STACKS -> computeStacks(line)
            ParseState.FILL_STACKS -> fillStacks(line)
            ParseState.READ_INSTRUCTION -> readInstruction(line)
        }
    }

    private fun computeStacks(line: String) {
        stacks = List((line.length + 1) / 4) { ArrayDeque() }
        state = ParseState.FILL_STACKS
        parseLine(line)
    }

    private fun fillStacks(line: String) {
        if (endStacksRegex.matches(line)) {
            state = ParseState.READ_INSTRUCTION
        } else {
            line.chunked(4).forEachIndexed { index, stack ->
                stackRegex.find(stack)?.let {
                    stacks[index].addFirst(it.groupValues[1][0])
                }
            }
        }
    }

    private fun readInstruction(line: String) {
        line.takeIf { it.isNotBlank() }.let {
            instructionRegex.matchEntire(line)?.let {
                crane.move(
                    this,
                    it.groupValues[1].toInt(),
                    it.groupValues[2].toInt() - 1,
                    it.groupValues[3].toInt() - 1
                )
            }
        }
    }

    fun topOfTheStacks() = stacks.map { it.lastOrNull() ?: '_' }.joinToString("")
}

sealed class Crane {
    abstract fun move(supplyStacks: SupplyStacks, quantity: Int, from: Int, to: Int)

    object CrateMover9000 : Crane() {
        override fun move(supplyStacks: SupplyStacks, quantity: Int, from: Int, to: Int) {
            with(supplyStacks) {
                repeat(quantity) { stacks[to].addLast(stacks[from].removeLast()) }
            }
        }
    }

    object CrateMover9001 : Crane() {
        override fun move(supplyStacks: SupplyStacks, quantity: Int, from: Int, to: Int) {
            with(supplyStacks) {
                stacks[to].size.let { index ->
                    repeat(quantity) { stacks[to].add(index, stacks[from].removeLast()) }
                }
            }
        }
    }
}

fun part1() {
    val supplyStacks = SupplyStacks(Crane.CrateMover9000)
    supplyStacks.parseInputFile()
    println("Part 1 : ${supplyStacks.topOfTheStacks()}")
}

fun part2() {
    val supplyStacks = SupplyStacks(Crane.CrateMover9001)
    supplyStacks.parseInputFile()
    println("Part 2 : ${supplyStacks.topOfTheStacks()}")
}

fun main() {
    part1()
    part2()
}