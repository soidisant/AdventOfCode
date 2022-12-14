package comon

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

fun puzzleInputFile(year: Int, filename: String) =
    File(ClassLoader.getSystemResource("$year/$filename").file)

fun puzzleInputBufferedReader(year: Int, filename: String) =
    BufferedReader(InputStreamReader(FileInputStream(puzzleInputFile(year, filename))))