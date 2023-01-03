package advent2022.day13

import comon.puzzleInputBufferedReader

sealed class Packet : Comparable<Packet> {
    data class Item(val data: Int) : Packet() {
        override fun compareTo(other: Packet): Int = when (other) {
            is List -> List(mutableListOf(this)).compareTo(other)
            is Item -> data.compareTo(other.data)
        }

        override fun toString(): String {
            return data.toString()
        }
    }

    data class List(val data: MutableList<Packet> = mutableListOf()) : Packet() {
        override fun compareTo(other: Packet): Int =
            if (this == other) 0
            else when {
                data.isEmpty() -> -1
                other is Item -> compareTo(List(mutableListOf(other)))
                other is List -> {
                    data.forEachIndexed { index, packet ->
                        val otherData = other.data.getOrNull(index)
                        if (otherData == null) {
                            return 1
                        } else when (packet.compareTo(otherData)) {
                            -1 -> return -1
                            1 -> return 1
                        }
                    }
                    -1
                }
                else -> 1
            }

        override fun toString(): String {
            return data.toString().replace("\\s".toRegex(), "")
        }
    }
}

class DistressSignal {
    val packets = mutableListOf<Packet>()

    init {
        puzzleInputBufferedReader(2022, "day13.txt")
            .readLines().windowed(2, 3).forEach { (packet1, packet2) ->
                packets.add(parsePacket(packet1))
                packets.add(parsePacket(packet2))
            }
    }
}

fun parsePacket(packet: String): Packet.List {
    val lists = mutableListOf<Packet.List>()
    var currentList = -1
    packet.splitToSequence('[', ',').forEach {
        if (it.isEmpty()) {
            val newList = Packet.List()
            lists.add(newList)
            if (currentList != -1) {
                lists[currentList].data.add(newList)
            }
            currentList++
        } else {
            "^(\\d*)(]*)$".toRegex().find(it)?.groupValues?.let { (_, item, rightBrackets) ->
                if (item.isNotEmpty()) {
                    lists[currentList].data.add(Packet.Item(item.toInt()))
                }
                repeat(rightBrackets.count { it == ']' }) {
                    if (currentList > 0) {
                        lists.removeAt(currentList--)
                    }
                }
            }
        }
    }
    return lists.first()
}

fun main() {
    val distressSignal = DistressSignal()
    var sumOfIndices = 0
    distressSignal.packets.windowed(2, 2).forEachIndexed { index, (p1, p2) ->
        if (p1 < p2) {
            sumOfIndices += (index + 1)
        }
    }
    println("Part1: the sum of indices is $sumOfIndices")

    val divider1 = parsePacket("[[2]]")
    val divider2 = parsePacket("[[6]]")
    distressSignal.packets.add(divider1)
    distressSignal.packets.add(divider2)
    distressSignal.packets.sort()
    val decoderKey = (distressSignal.packets.indexOf(divider1) + 1) * (distressSignal.packets.indexOf(divider2) + 1)

    println("Part2: the decoder key is $decoderKey")
}
