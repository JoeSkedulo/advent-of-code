package day3

import readInput

fun main() {

    val alphabet = ('a'..'z').toMutableList() + ('A'..'Z').toMutableList()

    fun sacks(input: List<String>) : List<Rucksack> {
        return input.map { line ->
            Rucksack(
                one = line.substring(0, line.length / 2),
                two = line.substring(line.length / 2, line.length)
            )
        }
    }

    fun sacksPartTwo(input: List<String>) : List<List<String>> {
        return buildList {
            val sacks = input.toMutableList()
            do {
                add(sacks.take(3))
                repeat(3) {
                    sacks.removeFirst()
                }
            } while (sacks.isNotEmpty())
        }.toList()
    }

    fun part1(input: List<String>): Int {
        return sacks(input).sumOf { (one, two) ->
            val difference = one.toSet().intersect(two.toList().toSet()).first()
            alphabet.indexOf(difference) + 1
        }
    }

    fun part2(input: List<String>): Int {
        return sacksPartTwo(input)
            .map { group ->
                alphabet.toMutableList().apply {
                    group.forEach { sack ->
                        alphabet.subtract(sack.toList().toSet())
                            .forEach { item -> this.remove(item) }
                    }
                }.first()
            }.sumOf { common -> alphabet.indexOf(common) + 1 }
    }

    val input = readInput("day3/Day03")
    println(part1(input))
    println(part2(input))
}

data class Rucksack(
    val one: String,
    val two: String
)
