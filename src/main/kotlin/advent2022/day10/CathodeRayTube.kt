package advent2022.day10

import comon.puzzleInputBufferedReader


sealed class Instruction {
    abstract var duration: Int

    data class Noop(override var duration: Int = 1) : Instruction()
    data class AddX(val value: Int, override var duration: Int = 2) : Instruction()
}


data class CPU(var register: Int = 1) {
    val instructions = mutableListOf<Instruction>()
    val cycles = mutableListOf<Int>()
    val stack = mutableListOf<Instruction>()

    fun parseInstructions() {
        val instructionRegex = "(addx|noop)\\s?(-?\\d+)?".toRegex()
        puzzleInputBufferedReader(2022, "day10.txt").forEachLine { line ->
            instructionRegex.find(line)?.groupValues?.let { (_, cmd, arg) ->
                when (cmd) {
                    "addx" -> instructions.add(Instruction.AddX(arg.toInt()))
                    "noop" -> instructions.add(Instruction.Noop())
                }
            }
        }
    }

    fun execute(instruction: Instruction) {
        when (instruction) {
            is Instruction.AddX -> {
                register += instruction.value
            }
            else -> {}
        }
    }

    private fun computeStack() {
        stack.forEach {
            it.duration--
            if (it.duration == 0) {
                execute(it)
            }
        }
        stack.removeAll { it.duration == 0 }
        cycles.add(register)
    }

    fun compute() {
        var i = 1

        while (instructions.isNotEmpty()) {
            val instruction = instructions.first()
            computeStack()
            i++
            if (stack.isEmpty()) {
                stack.add(instruction)
                instructions.removeFirst()
            }

        }
        while (stack.isNotEmpty()) {
            i++
            computeStack()
        }
    }

    fun signalStrength(cycleNumber: Int) =
        cycleNumber * cycles[cycleNumber - 1]

    fun computeCRT() {
        var screen = mutableListOf<String>()
        var cursor = 0
        var line = ""
        cycles.forEach { cycle ->
            line = if (cursor in (cycle - 1)..(cycle + 1)) {
                "$line#"
            } else {
                "$line."
            }

            if (cursor == 39) {
                screen.add(line)
                line = ""
                cursor = 0
            } else
                cursor++
        }
        println(screen.joinToString("\n"))
    }
}

fun main() {
    val cpu = CPU()
    cpu.parseInstructions()
    cpu.compute()

    val sum = cpu.signalStrength(20) +
            cpu.signalStrength(60) +
            cpu.signalStrength(100) +
            cpu.signalStrength(140) +
            cpu.signalStrength(180) +
            cpu.signalStrength(220)

    println("Part 1 -> sum of signal strength is $sum")

    println("Part 2: screen ")
    cpu.computeCRT()
    println("it spells PZULBAUA :shrug:")

}

