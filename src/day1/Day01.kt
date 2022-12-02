package day1

import readInput

fun main() {

    fun elves(input: List<String>) : List<List<String>> {
        return buildList {
            val elves = input.toMutableList()
            do {
                add(elves.takeWhile { line -> line != "" })
                elves.removeFirst()
            } while (elves.isNotEmpty())
        }.toList()
    }

    fun part1(input: List<String>): Int {
        return elves(input)
            .maxOf { elf -> elf.sumOf { it.toInt() } }
    }

    fun part2(input: List<String>): Int {
        return elves(input)
            .map { elf -> elf.sumOf { it.toInt() } }
            .sortedDescending()
            .take(3)
            .sum()
    }

    val input = readInput("day1/Day01")
    println(part1(input))
    println(part2(input))
}
