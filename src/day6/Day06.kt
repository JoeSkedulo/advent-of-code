package day6

import Runner

fun main() {
    Day6Runner().solve()
}

class Day6Runner : Runner<Int>(
    day = 6,
    expectedPartOneTestAnswer = 5,
    expectedPartTwoTestAnswer = null
) {

    override fun partOne(input: List<String>, test: Boolean): Int {
        return marker(input, 4)
    }

    override fun partTwo(input: List<String>, test: Boolean): Int {
        return marker(input, 14)
    }

    private fun marker(input: List<String>, size: Int) : Int {
        return input.first()
            .windowed(size = size)
            .mapIndexed { index, s -> Pair(index, s.toSet()) }
            .first { (_, s) -> s.size == size }
            .first + size
    }
}