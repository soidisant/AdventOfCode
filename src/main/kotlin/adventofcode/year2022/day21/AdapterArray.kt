package adventofcode.year2022.day21

import adventofcode.comon.puzzleInputFile
import adventofcode.comon.timeIt
import java.math.BigInteger
import java.security.InvalidParameterException

sealed class Operand {
    abstract fun calculable(): Boolean
    abstract fun calculate(): BigInteger
    abstract fun multiply(by: BigInteger): Operand
    abstract var value: BigInteger

    fun toVariable(): Variable? =
        if (this is Variable) this else null

    fun toOperation(): Operation? =
        if (this is Operation) this else null

    data class Variable(val name: String, var coeff: BigInteger = BigInteger.ONE) : Operand() {
        // avoid cast later
        override var value: BigInteger
            get() = error("")
            set(value) = error("")

        override fun calculable(): Boolean = false

        override fun calculate(): BigInteger {
            error("")
        }

        override fun multiply(by: BigInteger): Operand = this.also { coeff *= by }

        override fun toString(): String = if (coeff == BigInteger.ONE) name else "$coeff.$name"
    }

    data class Value(override var value: BigInteger) : Operand() {
        override fun toString(): String = "$value"
        override fun calculable(): Boolean = true
        override fun calculate(): BigInteger = value
        override fun multiply(by: BigInteger): Operand = this.also { value *= by }
    }

    data class Operation(
        var left: Operand,
        var right: Operand,
        var operator: (BigInteger, BigInteger) -> BigInteger
    ) : Operand() {
        override var value: BigInteger
            get() = error("")
            set(value) = error("")

        companion object {
            val plus: (BigInteger, BigInteger) -> BigInteger = BigInteger::plus
            val minus: (BigInteger, BigInteger) -> BigInteger = BigInteger::minus
            val times: (BigInteger, BigInteger) -> BigInteger = BigInteger::times
            val div: (BigInteger, BigInteger) -> BigInteger = BigInteger::div
        }

        override fun toString(): String =
            when (operator) {
                plus -> "($left + $right)"
                minus -> "($left - $right)"
                times -> "($left * $right)"
                div -> "($left / $right)"
                else -> "$left $operator $right"
            }

        override fun calculable() = left.calculable() && right.calculable()
        override fun calculate() = operator(left.calculate(), right.calculate())
        override fun multiply(by: BigInteger): Operand {
            when (operator) {
                times -> {
                    if (left is Value) {
                        left.multiply(by)
                    } else if (right is Value) {
                        right.multiply(by)
                    } else if (left is Variable) {
                        left.multiply(by)
                    } else if (right is Variable) {
                        right.multiply(by)
                    } else {
                        left.multiply(by)
                    }
                }
                div -> left.multiply(by)
                else -> {
                    left.multiply(by)
                    right.multiply(by)
                }
            }
            return this
        }
    }
}

data class Equation(
    var leftHand: Operand,
    var rightHand: Operand
) {
    override fun toString(): String = "$leftHand = $rightHand"

    fun solve() {
        if (leftHand is Operand.Value) {
            if (rightHand is Operand.Operation) {
                val operation = rightHand.toOperation()!!
                when (operation.operator) {
                    Operand.Operation.plus -> {
                        if (operation.left is Operand.Value) {
                            leftHand.value -= operation.left.value
                            rightHand = operation.right
                        } else {
                            if (operation.right is Operand.Value) {
                                leftHand.value -= operation.right.value
                                rightHand = operation.left
                            }
                        }
                    }
                    Operand.Operation.minus -> {
                        if (operation.left is Operand.Value) {
                            leftHand.value = operation.left.value - leftHand.value
                            rightHand = operation.right
                        } else {
                            if (operation.right is Operand.Value) {
                                leftHand.value += operation.right.value
                                rightHand = operation.left
                            }
                        }
                    }
                    Operand.Operation.div -> {
                        if (operation.right is Operand.Value) {
                            leftHand.multiply(operation.right.value)
                            rightHand = operation.left
                        }
                    }
                    Operand.Operation.times -> {
                        if (operation.left is Operand.Value) {
                            rightHand = operation.right.multiply(operation.left.value)
                        } else if (operation.right is Operand.Value) {
                            rightHand = operation.left.multiply(operation.right.value)
                        }
                    }
                }
            } else if (rightHand is Operand.Variable) {
                leftHand.value /= (rightHand as Operand.Variable).coeff
                (rightHand as Operand.Variable).coeff = BigInteger.ONE
                val tmp = rightHand
                rightHand = leftHand
                leftHand = tmp
            }
            val solved = leftHand is Operand.Variable && rightHand is Operand.Value
            if (!solved) {
                solve()
            }
        }
        if (leftHand is Operand.Variable) {
            if (rightHand is Operand.Operation && rightHand.toOperation()!!.calculable()) {
                rightHand = Operand.Value(rightHand.calculate())
            }
        }
    }
}

class AdapterArray {
    val equations = mutableMapOf<String, Equation>()

    init {
        val operationRegex = "([a-z]+): ([a-z]+) ([*+-/]) ([a-z]+)".toRegex()
        val numberRegex = "([a-z]+): (-?\\d+)".toRegex()
        puzzleInputFile(2022, "day21.txt").forEachLine { line ->
//        puzzleInputFile(2022, "day21-example.txt").forEachLine { line ->
            operationRegex.find(line)?.destructured?.let { (name, op1, operatorString, op2) ->
                val operator: BigInteger.(BigInteger) -> BigInteger =
                    when (operatorString) {
                        "+" -> Operand.Operation.plus
                        "-" -> Operand.Operation.minus
                        "*" -> Operand.Operation.times
                        "/" -> Operand.Operation.div
                        else -> {
                            throw InvalidParameterException()
                        }
                    }

                val operand1 = if (numberRegex.matches(op1)) {
                    Operand.Value(op1.toBigInteger())
                } else Operand.Variable(op1)
                val operand2 = if (numberRegex.matches(op2)) {
                    Operand.Value(op2.toBigInteger())
                } else Operand.Variable(op2)
                val equation = Equation(Operand.Variable(name), Operand.Operation(operand1, operand2, operator))
                equations[name] = equation
            }
            numberRegex.find(line)?.destructured?.let { (name, value) ->
                val equation = Equation(Operand.Variable(name), Operand.Value(value.toBigInteger()))
                equations[name] = equation
            }
        }
    }

    fun findRootValue(): Equation {
        val root = equations["root"]!!
        root.rightHand.toOperation()!!.replaceVariables()
        root.solve()
        return root
    }

    fun findHumanValue(): Equation {
        val root = equations["root"]!!
        root.leftHand = Operand.Value(BigInteger.ZERO)
        root.rightHand.toOperation()!!.operator = Operand.Operation.minus
        equations.remove("humn")
        root.rightHand.toOperation()!!.replaceVariables()
        root.solve()
        return root
    }

    fun Operand.Operation.replaceVariables() {
        listOf(this::left, this::right).forEach { operand ->
            when (operand.get()) {
                is Operand.Variable -> {
                    equations[operand.get().toVariable()!!.name]
                        ?.let {
                            when (it.rightHand) {
                                is Operand.Value -> operand.set(it.rightHand)
                                is Operand.Operation -> {
                                    (it.rightHand as Operand.Operation).replaceVariables()
                                    operand.set(it.rightHand)
                                }
                                is Operand.Variable -> {}
                            }
                            replaceVariables()
                        }
                }
                is Operand.Value -> {}
                is Operand.Operation -> {
                    val operation = (operand.get() as Operand.Operation)
                    if (operation.calculable()) {
                        operand.set(Operand.Value(operation.calculate()))
                    } else {
                        operation.replaceVariables()
                    }
                }
            }
        }
    }
}

fun part1() {
    val adapterArray = AdapterArray()
    val monkey = adapterArray.findRootValue()
    println("Part1: $monkey")
}

fun part2() {
    val adapterArray = AdapterArray()
    val human = adapterArray.findHumanValue()
    println("Part2: $human")
}

fun main() {
    timeIt {
        part1()
    }
    timeIt {
        part2()
    }
}
