package adventofcode.year2022.day20

import adventofcode.comon.puzzleInputFile

class GrovePositioningSystem(decryptionKey: Int) {
    private val items: MutableList<Item> = mutableListOf()
    private var zero: Item
    private var zeroIndex: Int = 0

    init {
        var index = 0
        puzzleInputFile(2022, "day20.txt").forEachLine { line ->
            val item = Item(index++, line.toLong() * decryptionKey)
            items.add(item)
        }
        zero = items.first { it.value == 0L }
        updateZero()
    }

    private fun updateZero() {
        zeroIndex = items.indexOf(zero)
    }

    fun Item.mix() {
        if (value != 0L) {
            var nextIndex = ((items.indexOf(this) + value) % items.lastIndex).toInt()
            if (nextIndex == 0) {
                nextIndex = items.lastIndex
            } else if (nextIndex < 0) {
                nextIndex += items.lastIndex
            }
            items.remove(this)
            items.add(nextIndex, this)
        }
    }

    fun mix(times: Int = 1) {
        val itemsId = items.map { it.id }
        repeat(times) {
            itemsId.forEach { id ->
                val item = items.first { it.id == id }
                item.mix()
            }
        }
        updateZero()
    }

    fun valueAt(index: Int): Long =
        items[(index + zeroIndex) % items.size].value

    override fun toString(): String = items.map { it.value }.joinToString(", ")
}

data class Item(
    var id: Int,
    var value: Long
) {
    override fun toString() = "$value"
}

fun part1() {
    val gps = GrovePositioningSystem(1)
    gps.mix()
    val sum = gps.valueAt(1000) + gps.valueAt(2000) + gps.valueAt(3000)
    println("Part1 : grove coordinates $sum")
    println("${gps.valueAt(1000)} ${gps.valueAt(2000)} ${gps.valueAt(3000)}")
}

fun part2() {
    val gps = GrovePositioningSystem(811589153)
    gps.mix(10)
    val sum = gps.valueAt(1000) + gps.valueAt(2000) + gps.valueAt(3000)
    println("Part2 : grove coordinates $sum")
    println("${gps.valueAt(1000)} ${gps.valueAt(2000)} ${gps.valueAt(3000)}")
}

fun main() {
    part1()
    part2()
}
