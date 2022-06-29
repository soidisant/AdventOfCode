package day7

import java.io.File

class Bag(val bagColor: String, val bagContentString: String) {

    companion object {
        val bagRules = mutableListOf<Bag>()
        val contentRegex = "(?<number>\\d+) (?<color>.*) bags?".toRegex()
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
}

fun parseRules() {
    val regex = "(?<color>.*) bags contain (?<content>.*).".toRegex()
    val file = File(ClassLoader.getSystemResource("day7input.txt").file)
    file.forEachLine { line ->
        regex.find(line)?.let {
            val color = it.groups["color"]!!.value
            val content = it.groups["content"]!!.value
//           println("$color | $content")
            Bag.bagRules.add(Bag(color, content))
        }
    }
}

fun ruleCanContain(quantity: Int, bagColor: String, bag: Bag): String? {
//    println("ruleCanContain $quantity $bagColor ${rule.bagColor}")
    bag.bagContent.forEach { enclosedBag ->
        if (enclosedBag.first.bagColor == bagColor && enclosedBag.second >= quantity) {
            return bag.bagColor
        } else {
            ruleCanContain(quantity, bagColor, enclosedBag.first)?.let {
                return bag.bagColor
            }
        }
    }
    return null
}

fun part1(quantity: Int, bagColor: String) {
    var directContent = mutableListOf<String>()
    Bag.bagRules.forEach {
        ruleCanContain(quantity, bagColor, it)?.let {
            directContent.add(it)
        }
    }

    println("$directContent")
    println(directContent.size)
    println("${directContent.distinct().size} bags can contain $quantity $bagColor bags")
}

fun part2(bagColor: String) {

    Bag.bagRules.filter { it.bagColor == bagColor }.firstOrNull()?.let { bag ->
        println("part 2: " + bag.numberOfIncludedBags)
    }
}

fun main() {
    parseRules()
//    println(rules)
    part1(1, "shiny gold")

    part2("shiny gold")
}
