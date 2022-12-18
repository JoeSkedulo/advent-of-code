package day17

import Runner
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day17Runner().solve()
}

class Day17Runner : Runner<Long>(
    day = 17,
    expectedPartOneTestAnswer = 3068,
    expectedPartTwoTestAnswer = 1514285714288
) {

    override fun partOne(input: List<String>, test: Boolean): Long {
        val directions = jetDirections(input)
        val shapes = RockShapeType.values().map { type -> type.toRockShape() }

        return dropRocks(
            number = 2022,
            shapes = shapes,
            directions = directions
        ).sum() + 1
    }

    private fun dropRocks(
        number: Int,
        shapes: List<RockShape>,
        directions: List<JetDirection>
    ) : List<Long> {
        val cave = Cave()
        val heightIncreases = mutableListOf<Long>()
        var jetNumber = 0

        repeat(number) { rockNumber ->
            val shape = shapes[rockNumber % shapes.size]
            var fallingRock = shape.toFallingRock(cave.rockStartingHeight())

            do {
                val direction = directions[jetNumber % directions.size]

                fallingRock = fallingRock.fall()

                if (cave.rockCanBePushed(fallingRock, direction)) {
                    fallingRock = fallingRock.push(direction)
                }

                jetNumber++
            } while (cave.rockCanFall(fallingRock))

            val currentHeight = cave.highestRock() ?: 0
            cave.addFallenRock(fallingRock)
            val newHeight = cave.highestRock()!!
            heightIncreases.add(newHeight - currentHeight)
        }

        return heightIncreases
    }

    override fun partTwo(input: List<String>, test: Boolean): Long {
        val directions = jetDirections(input)
        val shapes = RockShapeType.values().map { type -> type.toRockShape() }
        val initialHeightIncreases = dropRocks(
            number = 10000,
            shapes = shapes,
            directions = directions
        )

        val totalRocks = 1000000000000
        val commonSequence = initialHeightIncreases.findCommonHeightSequence()
        val sequenceCountForTotalRocks = totalRocks / commonSequence.sequence.length
        val rocksRemainingAfterLastSequence = (totalRocks - commonSequence.startIndex) % sequenceCountForTotalRocks
        val overallSequenceScore = sequenceCountForTotalRocks * commonSequence.sequence.map { c -> c.toString().toLong() }.sum()
        val beforeFirstSequenceScore = initialHeightIncreases.subList(0, commonSequence.startIndex).sum()
        val remainingPartialSequence = commonSequence.sequence.substring(0, rocksRemainingAfterLastSequence.toInt())
        val afterLastFullSequenceScore = remainingPartialSequence.map { c -> c.toString().toLong() }.sum() + 1

        return beforeFirstSequenceScore + overallSequenceScore + afterLastFullSequenceScore
    }

    /*
     * Mostly a fluke?
     *
     * Both the test and input data do not have a repeating sequence initially, so reverse the string and attempt
     * to find a repeating sequence by comparing a substring to splits of the overall string, if more than one substring
     * is found within the splits the sequence repeats.
     *
     * This only works when the initial generated number of rock falls is >= ~5400, unsure why
     */
    private fun List<Long>.findCommonHeightSequence() : CommonHeightSequence = joinToString("").let { source ->
        val reversed = source.reversed()
        var currentIndex = source.length
        var substring = reversed.substring(0, currentIndex)
        var windowed = reversed.windowed(substring.length, substring.length)
        var found = windowed.count { it == substring }

        while (found < 2) {

            substring = reversed.substring(0, currentIndex)
            windowed = reversed.windowed(substring.length, substring.length)
            found = windowed.count { it == substring }

            if (found < 2) {
                currentIndex--
            }
        }

        val startIndex = source.length - found * substring.length

        return CommonHeightSequence(
            sequence = substring.reversed(),
            startIndex = startIndex
        )
    }

    private fun Cave.rockStartingHeight() : Long {
        return highestRock()
            ?.let { height -> height + 5 }
            ?: 4
    }

    private fun Cave.highestRock() : Long? {
        val lastStacks = stacks.mapNotNull { stack ->
            stack.lastOrNull { space -> space is Rock }?.y?.toLong()
        }
        return lastStacks.maxOrNull()
    }

    private fun Cave.addFallenRock(rock: FallingRock) {
        val toFill = rock.spacesToFill()
        toFill.forEach { space ->
            val currentSpace = stacks[space.x].getOrNull(space.y)
            when {
                currentSpace == null -> stacks[space.x].add(space)
                space is Rock -> stacks[space.x][space.y] = space
                !(space is Empty && currentSpace is Rock) -> stacks[space.x][space.y] = space
            }
        }
    }

    private fun FallingRock.spacesToFill() : List<Space> {

        fun fill(verticalSpace: Int) : List<Space> = buildList {
            repeat(7) { x ->
                val yMin = area.minOf { rock -> rock.y }
                (yMin..yMin + verticalSpace).forEach { y ->
                    add(area.get(x = x, y = y) ?: Empty(x = x, y = y))
                }
            }
        }

        return when (type) {
            RockShapeType.Horizontal -> fill(0)
            RockShapeType.Cross -> fill(2)
            RockShapeType.Corner -> fill(2)
            RockShapeType.Vertical -> fill(3)
            RockShapeType.Square ->  fill(1)
        }
    }

    private fun List<Rock>.get(x: Int, y: Int) : Rock? {
        return firstOrNull { rock -> rock.x == x && rock.y == y }
    }

    private fun FallingRock.push(direction: JetDirection) : FallingRock {
        val newArea = when (direction) {
            JetDirection.Left -> {
                val xMin = area.minOf { rock -> rock.x }
                if (xMin > 0) {
                    area.map { rock ->
                        rock.copy(x = rock.x - 1, y = rock.y )
                    }
                } else {
                    area
                }
            }
            JetDirection.Right -> {
                val xMax = area.maxOf { rock -> rock.x }
                if (xMax < 6) {
                    area.map { rock ->
                        rock.copy(x = rock.x + 1, y = rock.y)
                    }
                } else {
                    area
                }
            }
        }
        return FallingRock(area = newArea, type = type)
    }

    private fun FallingRock.fall() : FallingRock {
        return FallingRock(
            area = area.map { rock ->
                rock.copy(x = rock.x , y = rock.y - 1)
            },
            type = type
        )
    }

    private fun RockShape.toFallingRock(startingHeight: Long) : FallingRock {
        return FallingRock(
            area = area.map { rock ->
                rock.copy(
                    x = rock.x + 2,
                    y = rock.y + startingHeight.toInt()
                )
            }.toMutableList(),
            type = type
        )
    }

    private fun Cave.rockCanFall(rock: FallingRock) : Boolean {
        return rock.area.fold(true) { acc, r ->
            if (!acc) {
                false
            } else {
                if (r.y == 0) {
                    false
                } else {
                    getSpace(x = r.x, y = r.y - 1)?.let { space -> space is Empty } ?: true
                }
            }
        }
    }

    private fun Cave.rockCanBePushed(rock: FallingRock, nextDirection: JetDirection) : Boolean {
        return rock.area.fold(true) { acc, r ->
            val nextX = when (nextDirection) {
                JetDirection.Left -> max(0, r.x - 1)
                JetDirection.Right -> min(6, r.x + 1)
            }
            if (!acc) {
                false
            } else {
                getSpace(x = nextX, y = r.y)?.let { space -> space is Empty } ?: true
            }
        }
    }

    private fun Cave.getSpace(x: Int, y: Int) : Space? {
        return stacks[x].getOrNull(y)
    }

    private fun jetDirections(input: List<String>) : List<JetDirection> {
        return input.first().map { c ->
            when (c) {
                '<' -> JetDirection.Left
                '>' -> JetDirection.Right
                else -> throw RuntimeException()
            }
        }
    }

    private fun RockShapeType.toRockShape() : RockShape {
        return RockShape(
            area = when (this) {
                RockShapeType.Horizontal -> {
                    buildList {
                        add(Rock(x = 0, y = 0))
                        add(Rock(x = 1, y = 0))
                        add(Rock(x = 2, y = 0))
                        add(Rock(x = 3, y = 0))
                    }
                }
                RockShapeType.Cross -> {
                    buildList {
                        add(Rock(x = 1, y = 0))
                        add(Rock(x = 0, y = 1))
                        add(Rock(x = 1, y = 1))
                        add(Rock(x = 2, y = 1))
                        add(Rock(x = 1, y = 2))
                    }
                }
                RockShapeType.Corner -> {
                    buildList {
                        add(Rock(x = 0, y = 0))
                        add(Rock(x = 1, y = 0))
                        add(Rock(x = 2, y = 0))
                        add(Rock(x = 2, y = 1))
                        add(Rock(x = 2, y = 2))
                    }
                }
                RockShapeType.Vertical -> {
                    buildList {
                        add(Rock(x = 0, y = 0))
                        add(Rock(x = 0, y = 1))
                        add(Rock(x = 0, y = 2))
                        add(Rock(x = 0, y = 3))
                    }
                }
                RockShapeType.Square -> {
                    buildList {
                        add(Rock(x = 0, y = 0))
                        add(Rock(x = 1, y = 0))
                        add(Rock(x = 0, y = 1))
                        add(Rock(x = 1, y = 1))
                    }
                }
            },
            type = this
        )
    }

}

enum class JetDirection {
    Left, Right
}

enum class RockShapeType {
    Horizontal, Cross, Corner, Vertical, Square
}

data class FallingRock(
    val area: List<Rock>,
    val type: RockShapeType
)

data class RockShape(
    val area : List<Rock>,
    val type: RockShapeType
)

sealed class Space(
    open val x: Int,
    open val y: Int
)

data class Rock(
    override val x: Int,
    override val y: Int
) : Space(x = x, y = y)

data class Empty(
    override val x: Int,
    override val y: Int
) : Space(x = x, y = y)

data class CommonHeightSequence(
    val sequence: String,
    val startIndex: Int
)

data class Cave(
    val stacks: List<MutableList<Space>> = buildList { repeat(7) { add(mutableListOf()) } }
)