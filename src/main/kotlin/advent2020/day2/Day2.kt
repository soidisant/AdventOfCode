package advent2020.day2

import java.io.File

val reg = "^(?<min>\\d+)-(?<max>\\d+) (?<letter>[a-z]): (?<password>[a-z]+)$".toRegex()

fun validate(line: String): Boolean {
    return reg.matchEntire(line)?.destructured?.let { (min, max, letter, password) ->
        password.count { it == letter[0] } in min.toInt()..max.toInt()
    } ?: false
}

fun validatePartTwo(line: String): Boolean {
    if (line.matches(reg)) {
        reg.find(line).let { match ->
            val password = match!!.groups["password"]!!.value
            val letter = match!!.groups["letter"]!!.value[0]
            val min = match!!.groups["min"]!!.value.toInt().dec().let {
                if (it < password.length) {
                    password[it] == letter
                } else {
                    false
                }
            }
            val max = match!!.groups["max"]!!.value.toInt().dec().let {
                if (it < password.length) {
                    password[it] == letter
                } else {
                    false
                }
            }
            return min xor max
        }
    }
    return false
}

fun main() {
    val file = File(Thread.currentThread().contextClassLoader.getResource("2020/day2input.txt").path)
    var count = 0
    var countPart2 = 0
    file.forEachLine {
        if (validate(it)) {
            count++
        }
        if (validatePartTwo(it)) {
            countPart2++
        }
    }
    println("compareAssignements $count")
    println("part2 $countPart2")
}
