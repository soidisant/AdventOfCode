package advent2020.day2

import java.io.File

val reg = "^(?<min>\\d+)-(?<max>\\d+) (?<letter>[a-z]): (?<password>[a-z]+)$".toRegex()

fun validate(line: String): Boolean {

    return reg.matchEntire(line)?.destructured?.let { (min, max, letter, password) ->
        password.filter { it == letter[0] }.count() in min.toInt()..max.toInt()
    } ?: false
}

fun validatePartTwo(line: String): Boolean {
    if (line.matches(reg)) {
        reg.find(line).let { match ->
            val password = match!!.groups.get("password")!!.value
            val letter = match!!.groups.get("letter")!!.value[0]
            val min = match!!.groups.get("min")!!.value.toInt().dec().let {
                if (it < password.length) {
                    password[it] == letter
                } else
                    false
            }
            val max = match!!.groups.get("max")!!.value.toInt().dec().let {
                if (it < password.length) {
                    password[it] == letter
                } else
                    false
            }
            return min xor max
        }
    }
    return false
}

fun main() {

    var file = File(Thread.currentThread().contextClassLoader.getResource("2020/day2input.txt").path)
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
