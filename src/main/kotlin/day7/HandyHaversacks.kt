package day7

import java.io.File

class Bag(val bagColor: String, val bagContentString: String) {

    companion object {
        val contentRegex = "(?<number>\\d+) (?<color>.*) bags?".toRegex()
        val bagRegex = "(?<color>.*) bags contain (?<content>.*).".toRegex()

        val bagRules: MutableList<Bag> by lazy {
            mutableListOf<Bag>().also { list ->
                val file = File(ClassLoader.getSystemResource("day7input.txt").file)
                file.forEachLine { line ->
                    bagRegex.find(line)?.let {
                        val color = it.groups["color"]!!.value
                        val content = it.groups["content"]!!.value
                        list.add(Bag(color, content))
                    }
                }
            }
        }
    }

    val bagContent: MutableList<Pair<Bag, Int>> by lazy {
        initBagContent()
    }

    fun initBagContent(): MutableList<Pair<Bag, Int>> {
        val bagContent = mutableListOf<Pair<Bag, Int>>()
        bagContentString.split(",").forEach {
            contentRegex.findAll(it).forEach {
                val number = it.groups["number"]!!.value.toInt()
                val color = it.groups["color"]!!.value
                bagRules.filter { it.bagColor == color }.firstOrNull()?.let { bag ->
                    bagContent.add(Pair(bag, number))
                }
            }
        }
        return bagContent
    }

    fun containsBag(): Boolean = bagContent.isNotEmpty()

    override fun toString(): String {
        return "$bagColor {" + bagContent.joinToString(";") { "${it.second} ${it.first.bagColor}" } + "}"
    }

    val numberOfIncludedBags: Int by lazy {
        var total = 0
        bagContent.forEach { bagPair ->
            if (bagPair.first.containsBag()) {
                val subtotal = bagPair.first.numberOfIncludedBags
                total += bagPair.second + bagPair.second * subtotal
            } else {
                total += bagPair.second
            }
        }
        total
    }

    fun canBagContain(quantity: Int, color: String): String? {
        bagContent.forEach { enclosedBag ->
            if (enclosedBag.first.bagColor == color && enclosedBag.second >= quantity) {
                return this.bagColor
            } else {
                enclosedBag.first.canBagContain(quantity, color)?.let {
                    return bagColor
                }
            }
        }
        return null
    }
}

fun part1(quantity: Int, bagColor: String) {
    val directContent = mutableListOf<String>()
    Bag.bagRules.forEach {
        it.canBagContain(quantity, bagColor)?.let {
            directContent.add(it)
        }
    }

    println("$directContent")
    println("Part 1: ${directContent.size} bags can contain $quantity $bagColor bags")
}

fun part2(bagColor: String) {

    Bag.bagRules.filter { it.bagColor == bagColor }.firstOrNull()?.let { bag ->
        println("Part 2: one $bagColor bag contains ${bag.numberOfIncludedBags} bags")
    }
}

fun main() {
    part1(1, "shiny gold")
    part2("shiny gold")
}
