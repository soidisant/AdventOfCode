package day6

import java.io.File

fun part1() {

    val file = File(ClassLoader.getSystemResource("day6input.txt").file)
    var count = 0
    val groupanswer = mutableSetOf<Char>()
    file.forEachLine { line ->
        if (line.isNotEmpty()) {
            groupanswer.addAll(line.toCharArray().toSet())
//            println("$line $groupanswer")
        } else {
            count += groupanswer.size
            groupanswer.clear()
        }
    }
    println(groupanswer)
    count += groupanswer.size
    println("Part 1 $count yes answers")
}

fun part2() {

    val file = File(ClassLoader.getSystemResource("day6input.txt").file)
    var count = 0
    val groupanswer = mutableListOf<Set<Char>>()
    file.forEachLine { line ->
        if (line.isNotEmpty()) {
            groupanswer.add(line.toCharArray().toSet())
//            println("$line $groupanswer")
        } else {
            val everyoneYes = groupanswer.flatten().groupBy {
                it
            }.filter {
                it.value.size == groupanswer.size
            }
//            println(everyoneYes)
            count += everyoneYes.keys.size
            groupanswer.clear()
        }
    }
    val everyoneYes = groupanswer.flatten().groupBy {
        it
    }.filter {
        it.value.size == groupanswer.size
    }.keys.count()

    count += everyoneYes
    println("Part2 $count yes answers")
}

fun main() {
    part1()
    part2()
}
