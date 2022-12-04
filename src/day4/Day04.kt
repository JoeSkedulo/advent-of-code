package day4

import Runner
fun main() {
    Day4Runner().solve()
}

class Day4Runner : Runner(
    day = 4,
    expectedPartOneTestAnswer = 2,
    expectedPartTwoTestAnswer = 4
) {

    override fun partOne(input: List<String>): Int {
        return input.map { line ->
            line.toAssignments()
        }.count { (first, second) ->
            first.sectionOne <= second.sectionOne && first.sectionTwo >= second.sectionTwo
                    || second.sectionOne <= first.sectionOne && second.sectionTwo >= first.sectionTwo
        }
    }

    override fun partTwo(input: List<String>): Int {
        return input.map { line ->
            line.toAssignments()
        }.count { (first, second) ->
            !(first.sectionTwo < second.sectionOne || second.sectionTwo < first.sectionOne)
        }
    }

    private fun String.toAssignments() : List<Assignment> {
        val (first, second) = this.split(",").map { sections -> sections.split("-") }
        return listOf(
            Assignment(
                sectionOne = first.first().toInt(),
                sectionTwo = first.last().toInt()
            ),
            Assignment(
                sectionOne = second.first().toInt(),
                sectionTwo = second.last().toInt()
            )
        )
    }
}

data class Assignment(
    val sectionOne: Int,
    val sectionTwo: Int
)