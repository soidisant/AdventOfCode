package advent2020.day8

import java.io.File

class HandheldHalting {

    companion object {
        val instructionRegex = "(?<cmd>nop|acc|jmp) (?<arg>[+-]\\d+)".toRegex()

        enum class COMMAND {
            NOP, ACC, JUMP
        }
    }

    fun COMMAND.isFixable() = this == COMMAND.NOP || this == COMMAND.JUMP

    fun COMMAND.fix() = when (this) {
        COMMAND.NOP -> COMMAND.JUMP
        COMMAND.JUMP -> COMMAND.NOP
        else -> this
    }

    fun String.toCOMMAND() =
        when (this) {
            "nop" -> COMMAND.NOP
            "acc" -> COMMAND.ACC
            "jmp" -> COMMAND.JUMP
            else -> null
        }

    var accumulator = 0

    private var instructionPointer = 0

    data class Instruction(var cmd: COMMAND, val argument: Int, var calls: Int = 0)

    fun Instruction.fix() {
        cmd = cmd.fix()
    }

    class InfiniteLoopException : Exception("Infinite loop!")

    fun Instruction.execute() {
        if (calls != 0) {
            throw InfiniteLoopException()
        }
        calls++
        when (cmd) {
            COMMAND.ACC -> {
                accumulator += argument
                instructionPointer++
            }
            COMMAND.NOP -> {
                instructionPointer++
            }
            COMMAND.JUMP -> instructionPointer += argument
        }
    }

    val instructionStack: List<Instruction> by lazy {
        mutableListOf<Instruction>().also { stack ->
            File(ClassLoader.getSystemResource("2020/day8input.txt").file).let { file ->
                file.forEachLine { line ->
                    instructionRegex.find(line)?.let {
                        stack.add(
                            Instruction(
                                it.groups["cmd"]!!.value.toCOMMAND()!!,
                                it.groups["arg"]!!.value.toInt()
                            )
                        )
                    }
                }
            }
        }.toList()
    }

    fun reset() {
        accumulator = 0
        instructionPointer = 0
        instructionStack.forEach {
            it.calls = 0
        }
    }

    fun simulate() {
        instructionPointer = 0
        accumulator = 0
        while (instructionPointer < instructionStack.size) {
            try {
                instructionStack[instructionPointer].execute()
            } catch (e: InfiniteLoopException) {
                println(e.message + " Accumulator = $accumulator")
                return
            }
        }
    }

    fun fixLoop(): String {
        reset()
        var flipIndex = -1

        while (instructionPointer < instructionStack.size) {
            try {
                instructionStack[instructionPointer].execute()
            } catch (e: InfiniteLoopException) {
//                println(e.message + " Accumulator = $accumulator")
                instructionStack.withIndex().firstOrNull { (index, instruction) ->
                    index > flipIndex && instruction.cmd.isFixable()
                }?.let {
                    if (flipIndex != -1) {
                        // previous fix was not successful setting back to the original value
                        instructionStack[flipIndex].fix()
                    }
                    flipIndex = it.index
                    it.value.fix()
                    reset()
                }
                    ?: throw Exception("unable to fix program :(") // no more instruction can be switched (we cannot fix the program)
            }
        }
        if (flipIndex != -1)
            return "fix was applied @line ${flipIndex + 1} \n-${instructionStack[flipIndex].cmd.fix()} ${instructionStack[flipIndex].argument}  \n+${instructionStack[flipIndex].cmd} ${instructionStack[flipIndex].argument}"
        else
            return "programs runs fine no need fixing"
    }
}

fun main() {
    val handheldHalting = HandheldHalting()

    // compareAssignements
    println("part 1 : ")
    handheldHalting.simulate()
    println("part 2 : ")
    handheldHalting.fixLoop().runCatching {
        println(this)
        println("accumulator = " + handheldHalting.accumulator)
    }.onFailure {
        println(it.message)
    }
}
