package advent2022.day6

import comon.puzzleInputBufferedReader
import java.io.BufferedReader


class BufferedReaderIterator(private val reader: BufferedReader) : Iterator<Int> {
    override fun hasNext(): Boolean = reader.ready()
    override fun next(): Int = reader.read()
}

fun detectStartOfPacket(packetSize: Int) {
    puzzleInputBufferedReader(2022, "day6.txt").use { reader ->
        val iterator = BufferedReaderIterator(reader)
        val sequence = iterator.asSequence()
        sequence.windowed(packetSize).withIndex().first { (index, values) ->
            values.distinct().size == packetSize
        }.let {
            val index = it.index + packetSize
            val packet = it.value.joinToString("") { it.toChar().toString() }
            println("packet $packet of size $packetSize found at index $index")
        }

    }
}

fun main() {
    detectStartOfPacket(4)
    detectStartOfPacket(14)

}