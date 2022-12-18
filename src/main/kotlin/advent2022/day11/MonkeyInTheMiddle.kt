package advent2022.day11

import comon.puzzleInputBufferedReader
import comon.timeIt

sealed class Operation<T : Number> {
    abstract fun invoke(firstOperand: T): T
    abstract val operation: T.(T) -> T

    data class Unary<T : Number>(override val operation: T.(T) -> T) : Operation<T>() {
        override fun invoke(firstOperand: T): T = operation(firstOperand, firstOperand)
    }

    data class Binary<T : Number>(override val operation: T.(T) -> T, val secondOperand: T) : Operation<T>() {
        override fun invoke(firstOperand: T): T = operation(firstOperand, secondOperand)
    }
}

fun Long.divisibleBy(other: Long) = this % other == 0L

data class TestOperation<T : Number>(
    val operation: T.(T) -> Boolean,
    val operand: T,
    val trueMonkey: Int,
    val falseMonkey: Int
)

inline fun <T> List<T>.nIndexOfFirst(n: Int, predicate: (T) -> Boolean): Int? {
    var index = 0
    var found = 0
    for (item in this) {
        if (predicate(item)) {
            if (++found == n)
                return index
        }
        index++
    }
    return null
}

class Monkey(
    val name: Int,
    val operation: Operation<Long>,
    val testOperation: TestOperation<Long>,
    val items: MutableList<Long> = mutableListOf()
) {
    var inspected = 0L
    fun raiseWorryLevel(worryLevel: Long) = operation.invoke(worryLevel)
    fun nextMonkey(item: Long) = if (testOperation.operation(
            item,
            testOperation.operand
        )
    ) testOperation.trueMonkey else testOperation.falseMonkey

    fun inspectItems(manageWorryLevel: (Long) -> Long, throwItem: (Long, Int) -> Unit) {
        items.forEach { item ->
            inspected++
            val newItemValue = manageWorryLevel(raiseWorryLevel(item))
            throwItem(newItemValue, nextMonkey(newItemValue))
        }
        items.clear()
    }
}

inline operator fun List<String>.component6() = get(index = 5)

class MonkeyInTheMiddle(val worryLevelFactor: Long?) {
    val monkeys = mutableListOf<Monkey>()
    private val manageWorryLevel: (Long) -> Long

    init {
        puzzleInputBufferedReader(2022, "day11.txt").readLines()
            .windowed(6, 7).forEach { (monkeyName, items, operation, test, trueAction, falseAction) ->
                monkeys.add(parseMonkey(monkeyName, items, operation, test, trueAction, falseAction))
            }

        val customWorryLevelReduceFact = monkeys.map { it.testOperation.operand }.reduce { a, b ->
            a * b
        }.toLong()
        manageWorryLevel = { worryLevel ->
            if (worryLevelFactor == null) {
                worryLevel % customWorryLevelReduceFact
            } else
                worryLevel / worryLevelFactor
        }
    }

    fun round() {
        monkeys.forEach { monkey ->
            monkey.inspectItems(manageWorryLevel) { item, nextMonkey ->
                monkeys[nextMonkey].items.add(item)
            }
        }
    }

    fun rounds(numberOfRounds: Int) {
        repeat(numberOfRounds) {
            round()
        }
        println(monkeys.map { it.inspected })
        val shenanigans = monkeys.sortedByDescending { it.inspected }
            .let { (m1, m2) -> m1.inspected * m2.inspected }
        println("The level of monkey business after ${numberOfRounds} rounds of stuff-slinging simian shenanigans is $shenanigans")

    }
}

fun parseMonkey(
    monkeyName: String,
    items: String,
    operationInput: String,
    testInput: String,
    trueAction: String,
    falseAction: String
): Monkey {
    val nameRegex = "Monkey (\\d+):".toRegex()
    val operationRegex = "Operation: new = (old) (\\*|\\+|||-) (old|[0-9]+)".toRegex()
    val testRegex = "Test: divisible by (\\d+)".toRegex()
    val monkeyRegex = "If (?>true|false): throw to monkey (\\d+)".toRegex()
    val itemsRegex = "(\\d+)".toRegex()

    val name = nameRegex.find(monkeyName)!!.groupValues[1].toInt()
    val operation = operationRegex.find(operationInput)!!.groupValues.let { (_, operand1, operator, operand2) ->
        val op: Long.(Long) -> Long = when (operator) {
            "+" -> Long::plus
            "-" -> Long::minus
            "*" -> Long::times
            else -> {
                throw IllegalArgumentException("")
            }
        }
        if (operand1 == operand2) {
            Operation.Unary(op)
        } else {
            Operation.Binary(op, operand2.toLong())
        }
    }

    val test = testRegex.find(testInput)!!.groupValues.let { (_, operand) ->
        TestOperation(
            Long::divisibleBy,
            operand.toLong(),
            monkeyRegex.find(trueAction)!!.groupValues[1].toInt(),
            monkeyRegex.find(falseAction)!!.groupValues[1].toInt()
        )
    }
    val items =
        itemsRegex.findAll(items)
            .map {
                it.groupValues[1].toLong()
            }
            .toMutableList()
    return Monkey(name, operation, test, items)
}

fun part1() {
    MonkeyInTheMiddle(3).rounds(20)
}

fun part2() {
    MonkeyInTheMiddle(null).rounds(10000)
}

fun main() {
    timeIt("Part1") { part1() }
    timeIt("Part2") { part2() }
}