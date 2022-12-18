package comon

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun puzzleInputFile(year: Int, filename: String) =
    File(ClassLoader.getSystemResource("$year/$filename").file)

fun puzzleInputBufferedReader(year: Int, filename: String) =
    BufferedReader(InputStreamReader(FileInputStream(puzzleInputFile(year, filename))))

@OptIn(ExperimentalTime::class)
fun timeIt(label: String? = null, block: () -> Unit) = measureTime {
    block.invoke()
}.let { println("${label?.let { "$label " }?:""}took $it to run") }
