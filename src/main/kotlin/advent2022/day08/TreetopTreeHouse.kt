package advent2022.day08

import comon.puzzleInputBufferedReader

data class Tree(val height: Int, val x: Int, val y: Int, val forest: Forest) {
    fun left() = runCatching { forest.trees[y][x - 1] }.getOrNull()
    fun right() = runCatching { forest.trees[y][x + 1] }.getOrNull()
    fun top() = runCatching { forest.trees[y - 1][x] }.getOrNull()
    fun bottom() = runCatching { forest.trees[y + 1][x] }.getOrNull()

    fun visibleFrom(direction: Tree.() -> Tree?): Boolean {
        var neighbor = direction.invoke(this)
        while (neighbor != null) {
            if (neighbor.height >= height) {
                return false
            }
            neighbor = direction.invoke(neighbor)
        }
        return true
    }

    fun visibility() =
        visibleFrom(Tree::left) || visibleFrom(Tree::right) || visibleFrom(Tree::top) || visibleFrom(Tree::bottom)

    fun visibleNeighbors(direction: Tree.() -> Tree?): Int {
        var neighbor = direction.invoke(this)
        var iSee = 0
        while (neighbor != null) {
            iSee++
            if (neighbor.height >= height) {
                return iSee
            }
            neighbor = direction.invoke(neighbor)
        }
        return iSee
    }

    fun scenicScore() =
        visibleNeighbors(Tree::top) * visibleNeighbors(Tree::bottom) * visibleNeighbors(Tree::left) * visibleNeighbors(
            Tree::right
        )
}


class Forest {
    val trees = mutableListOf<MutableList<Tree>>()
    val visibleTress = mutableListOf<Tree>()

    init {
        puzzleInputBufferedReader(2022, "day8.txt").useLines { lines ->
            lines.forEachIndexed { y, line ->
                if (line.isNotBlank()) {
                    val row = mutableListOf<Tree>()
                    trees.add(row)
                    line.forEachIndexed { x, h ->
                        row.add(Tree(h.digitToIntOrNull()!!, x, y, this))
                    }
                }
            }
        }
        trees.forEach { row ->
            row.forEach { tree ->
                if (tree.visibility())
                    visibleTress.add(tree)
            }
        }
    }

    fun print() {
        trees.forEach { row ->
            row.forEach { print(it.height) }
            println()
        }
    }

    fun printVisibility() {
        trees.forEach { row ->
            row.forEach { print(if (it.visibility()) "v" else "_") }
            println()
        }
    }

    fun maxScenicScore(): Int {
        var max = 0
        trees.forEach { row ->
            row.forEach { tree ->
                val score = tree.scenicScore()
                if (score > max)
                    max = score
            }
        }
        return max
    }
}

fun main() {
    val forest = Forest()
    println("The forest contains ${forest.visibleTress.count()} visible trees.")
    println("The max scenic score ${forest.maxScenicScore()}")
}