package day10

import Runner

fun main() {
    Day10Runner().solve()
}

class Day10Runner : Runner<Int>(
    day = 10,
    expectedPartOneTestAnswer = 13140,
    expectedPartTwoTestAnswer = null
) {

    override fun partOne(input: List<String>, test: Boolean): Int {
        val cycles = cycles(input)
        return listOf(20, 60, 100, 140, 180, 220)
            .sumOf { cycleIndex -> cycles[cycleIndex - 1].xValue * cycleIndex }
    }

    override fun partTwo(input: List<String>, test: Boolean): Int {
        val cycles = cycles(input)
        cycles.forEachIndexed { index, cycle ->
            if (index % 40 == 0) {
                println("")
            }
            if (cycle.xValue in (index % 40 - 1)..(index % 40 + 1)) {
                print("█")
            } else {
                print(" ")
            }
        }

        return 1
    }

    private fun cycles(input: List<String>) : List<Cycle> {
        val instructions = instructions(input)
        return buildList {
            add(Cycle(1))
            instructions.forEach { instruction ->
                addAll(instruction.toCycles(this.last().xValue))
            }
        }
    }

    private fun instructions(input : List<String>) : List<Instruction> {
        return input.map { line ->
            when (line) {
                "noop" -> Noop
                else -> Addx(value = line.split(" ").last().toInt())
            }
        }
    }

    private fun Instruction.toCycles(currentValue: Int) : List<Cycle> {
        return when (this) {
            is Addx -> listOf(Cycle(currentValue), Cycle(currentValue + this.value))
            Noop -> listOf(Cycle(currentValue))
        }
    }

}

sealed interface Instruction

object Noop : Instruction

data class Addx(val value: Int) : Instruction

data class Cycle(
    val xValue: Int
)