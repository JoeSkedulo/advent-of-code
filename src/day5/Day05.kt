package day5

import Runner

fun main() {
    Day5Runner().solve()
}

class Day5Runner : Runner<String>(
    day = 5,
    expectedPartOneTestAnswer = "CMZ",
    expectedPartTwoTestAnswer = "MCD"
) {

    override fun partOne(input: List<String>, test: Boolean): String {

        val moves = input.filter { line -> line.contains("move") }
        val stacks = buildStacks(input)

        moves.forEach { line ->
            val move = line.toMove()
            val startStack = stacks[move.startStack]
            val endStack = stacks[move.endStack]

            repeat(move.count) {
                endStack.add(startStack.last())
                startStack.removeLast()
            }
        }

        return stacks.toAnswer()
    }

    override fun partTwo(input: List<String>, test: Boolean): String {
        val moves = input.filter { line -> line.contains("move") }
        val stacks = buildStacks(input)

        moves.forEach { line ->
            val move = line.toMove()
            val startStack = stacks[move.startStack]
            val endStack = stacks[move.endStack]

            val removed = buildList {
                repeat(move.count) {
                    add(0, startStack.removeLast())
                }
            }
            endStack.addAll(removed)
        }

        return stacks.toAnswer()
    }

    private fun buildStacks(input: List<String>) : List<MutableList<String>> {
        val stackLines = input.filter { line -> line != "" && !line.contains("move") }.toMutableList()
        val lastStackLine = stackLines.removeLast()
        val reveredStackLines = stackLines.reversed()
        val stackCount = lastStackLine.filter { it.isDigit() }.map { it.intValue() }.max()
        return buildList {
            repeat(stackCount) {
                add(mutableListOf())
            }
            reveredStackLines.forEach { line ->
                line.windowed(4, 4, partialWindows = true).forEachIndexed { index, crates ->
                    crates
                        .filter { it.isLetter() }
                        .forEach { char ->
                            this[index].add(char.toString())
                        }
                }
            }
        }
    }

    private fun Char.intValue() = this.toString().toInt()

    private fun List<MutableList<String>>.toAnswer() = let { stacks ->
        buildString {
            stacks.forEach { stack ->
                append(stack.last())
            }
        }
    }

    private fun String.toMove() : Move {
        val filteredLine = filter { it.isDigit() || it.isWhitespace() }
            .split(" ")
            .mapNotNull { it.toIntOrNull() }
        return Move(
            count = filteredLine[0],
            startStack = filteredLine[1] - 1,
            endStack = filteredLine[2] - 1
        )
    }
}

data class Move(
    val count: Int,
    val startStack: Int,
    val endStack: Int
)