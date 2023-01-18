package adventofcode.comon

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

fun puzzleInputFile(year: Int, filename: String) =
    File(ClassLoader.getSystemResource("$year/$filename").file)

fun puzzleInputBufferedReader(year: Int, filename: String) =
    BufferedReader(InputStreamReader(FileInputStream(puzzleInputFile(year, filename))))

@OptIn(ExperimentalTime::class)
fun <T> timeIt(label: String? = null, block: () -> T) = measureTimedValue(block).let {
    println("${label?.let { "$label " } ?: ""}took ${it.duration} to run")
    it.value
}
