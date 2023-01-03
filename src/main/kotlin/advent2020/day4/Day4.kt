package advent2020.day4

import java.io.File

enum class Fields(val label: String) {
    BIRTH_YEAR("byr"),
    ISSUE_YEAR("iyr"),
    EXPIRATION_YEAR("eyr"),
    HEIGHT("hgt"),
    HAIR_COLOR("hcl"),
    EYE_COLOR("ecl"),
    PASSPORT_ID("pid"),
    COUNTRY_ID("cid")
}

data class PassPort(
    val byr: Int,
    val iyr: Int,
    val eyr: Int,
    val height: String,
    val hcl: String,
    val pid: String,
    val cid: String?
)



fun main() {
    val file = File(Thread.currentThread().contextClassLoader.getResource("2020/day4input.txt")!!.path)
    val reg = "(?<field>[a-z]+):(?<value>[\\w#]+)".toRegex()
    val fields = mutableListOf<String>()
    var valid = 0
    var lineNb = 0
    file.forEachLine { line ->
        lineNb++
        println(lineNb)
        if (line.isNullOrBlank()) {
//            println("$lineNb $fields")
//            println(Fields.values().filter { it != Fields.COUNTRY_ID }.map { it.label })
            if (fields.containsAll(Fields.values().filter { it != Fields.COUNTRY_ID }.map { it.label })) {
                valid++
            }
            fields.clear()
        } else {
            reg.findAll(line).forEach { result ->
                val field = result.groups["field"]!!.value
                val value = result.groups["value"]!!.value

                when (field) {
                    Fields.BIRTH_YEAR.label -> {
                        if (value.toInt() in 1920..2002)
                            fields.add(field)
                    }
                    Fields.ISSUE_YEAR.label -> if (value.toInt() in 2010..2020)
                        fields.add(field)
                    Fields.EXPIRATION_YEAR.label -> if (value.toInt() in 2020..2030)
                        fields.add(field)
                    Fields.HEIGHT.label -> {
                        if (value.endsWith("cm") && value.replace("cm", "").toInt() in 150..193) {
                            fields.add(field)
                        } else if (value.endsWith("in") && value.replace("in", "").toInt() in 59..76) {
                            fields.add(field)
                        }
                    }
                    Fields.HAIR_COLOR.label -> if ("#[0-9a-f]{6}".toRegex().matches(value))
                        fields.add(field)
                    Fields.EYE_COLOR.label -> if (value in listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth"))
                        fields.add(field)
                    Fields.PASSPORT_ID.label -> if ("[0-9]{9}".toRegex().matches(value))
                        fields.add(field)
                    Fields.COUNTRY_ID.label -> fields.add(field)
                    else -> {}
                }
            }
        }
    }
    if (fields.containsAll(Fields.values().filter { it != Fields.COUNTRY_ID }.map { it.label })) {
        valid++
    }
    println("number of valid passports $valid")
}
