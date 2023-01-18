import adventofcode.comon.puzzleInputBufferedReader

val values: List<Int> by lazy {
    val list = mutableListOf<Int>()
    puzzleInputBufferedReader(2020, "day1.txt").forEachLine { line ->
        list.add(line.toInt())
    }
    list
}

fun part1() {
    val pair = values.findPairOfSum(2020)!!
    println(pair)
    println(pair.first * pair.second)
}

fun part2() {
    val triple = values.firstNotNullOf { x ->
        val pair = values.findPairOfSum(2020 - x)
        if (pair != null) {
            Triple(x, pair.first, pair.second)
        } else null
    }

    println(triple)
    println(triple.first * triple.second * triple.third)
}

fun List<Int>.findPairOfSum(sum: Int): Pair<Int, Int>? {
    val complements = associateBy { sum - it }
    return values.firstNotNullOfOrNull { number ->
        val complement = complements[number]
        if (complement != null) {
            Pair(number, complement)
        } else null
    }
}

fun main() {
    part1()
    part2()
}
