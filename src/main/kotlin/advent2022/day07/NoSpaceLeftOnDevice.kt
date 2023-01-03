package advent2022.day07

import comon.puzzleInputBufferedReader
import kotlin.math.absoluteValue

class FileSystem {
    companion object {
        enum class Commands(val cmd: String, val exec: FileSystem.(String, List<String>) -> Unit) {
            CD("cd", FileSystem::cd),
            LS("ls", FileSystem::ls)
        }

        val cmdRegex = "(${Commands.values().map(Commands::cmd).joinToString("|")})(?>\\s+(.*))?".toRegex()
    }

    sealed class FileObject {
        abstract val name: String
        abstract val size: Int
        abstract val parent: Dir?

        class File(override val name: String, override val parent: Dir?, override val size: Int) : FileObject()

        class Dir(
            override val name: String,
            override val parent: Dir?,
            val children: MutableSet<FileObject> = mutableSetOf()
        ) : FileObject() {
            override val size: Int
                get() = children.sumOf { it.size }
        }
    }

    var currentDirectory = FileObject.Dir("/", null)
    val directories = mutableSetOf<FileObject.Dir>(currentDirectory)

    val root = directories.first { it.name == "/" && it.parent == null }

    fun cd(directory: String, output: List<String>) {
        when (directory) {
            "/" -> {
                currentDirectory = directories.first { it.name == "/" && it.parent == null }
            }
            ".." -> {
                currentDirectory.parent?.let {
                    currentDirectory = it
                }

            }
            else -> {
                currentDirectory.children.firstOrNull { it is FileObject.Dir && it.name == directory }?.let {
                    currentDirectory = it as FileObject.Dir
                } ?: run {
                    val newDir = FileObject.Dir(directory, currentDirectory)
                    currentDirectory.children.add(newDir)
                    directories.add(newDir)
                    currentDirectory = newDir
                }
            }
        }
    }

    fun ls(argument: String, output: List<String>) {
        output.forEach { line ->
            line.split(" ").let { (type, name) ->
                when (type) {
                    "dir" -> addDirectory(name)
                    else -> addFile(name, type.toInt())
                }

            }

        }
    }

    fun addDirectory(name: String) {
        if (!currentDirectory.children.any { it is FileObject.Dir && it.name == name }) {
            val newDir = FileObject.Dir(name, currentDirectory)
            currentDirectory.children.add(newDir)
            directories.add(newDir)
        }
    }

    fun addFile(name: String, size: Int) {
        if (!currentDirectory.children.any { it is FileObject.File && it.name == name }) {
            val newFile = FileObject.File(name, currentDirectory, size)
            currentDirectory.children.add(newFile)
        }
    }
}

enum class ParseStatus {
    COMMAND,
    OUTPUT,
}

fun FileSystem.parseFile() {
    var command: Pair<FileSystem.Companion.Commands, String>? = null
    val commandOutput = mutableListOf<String>()
    puzzleInputBufferedReader(2022, "day7.txt").use { reader ->
        reader.forEachLine { line ->
            if (line.isNotBlank()) {
                val status = if (line.startsWith("$")) ParseStatus.COMMAND else ParseStatus.OUTPUT
                when (status) {
                    ParseStatus.COMMAND -> {
                        if (command != null) {
                            command!!.first.exec.invoke(this, command!!.second, commandOutput)
                            commandOutput.clear()
                        }
                        command = parseCommand(line)
                    }
                    ParseStatus.OUTPUT -> {
                        commandOutput.add(line)
                    }
                }
            }
        }
        if (command != null && commandOutput.isNotEmpty()) {
            command!!.first.exec.invoke(this, command!!.second, commandOutput)
        }
    }
}

fun FileSystem.parseCommand(command: String) =
    FileSystem.cmdRegex.find(command)?.groupValues?.let { (_, command, argument) ->
        FileSystem.Companion.Commands.values().first { it.cmd == command } to argument
    } ?: throw IllegalArgumentException()


fun main() {
    val fileSystem = FileSystem()
    fileSystem.parseFile()
    val part1 = fileSystem.directories.map { it.size }.filter { it <= 100000 }.sum()
    println("part1 : sum is $part1")

    val neededSpace = (70000000 - 30000000 - fileSystem.root.size).absoluteValue
    val sizeOfDirectoryToDelete =
        fileSystem.directories.map { it.size }.filter { it >= neededSpace }.minOf { it }
    println("part2 : $sizeOfDirectoryToDelete")
}

