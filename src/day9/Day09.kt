package day9

import Runner
import day9.Direction.*
import kotlin.math.abs

fun main() {
    Day9Runner().solve()
}

class Day9Runner : Runner<Int>(
    day = 9,
    expectedPartOneTestAnswer = 13,
    expectedPartTwoTestAnswer = 1
) {

    override fun partOne(input: List<String>): Int {
        return getPositions(input)
            .filter { knot -> knot.index == 1 }
            .distinctBy { knot -> "${knot.x}:${knot.y}" }
            .count()
    }

    override fun partTwo(input: List<String>): Int {
        return getPositions(input, knots = 10)
            .filter { knot -> knot.index == 9 }
            .distinctBy { knot -> "${knot.x}:${knot.y}" }
            .count()
    }

    private fun getPositions(
        input: List<String>,
        knots: Int = 2
    ) : List<KnotPosition> {
        return buildList {
            addAll(initialPositions(knots))
            moves(input).forEach { move ->
                addAll(moveRope(this, move, knots))
            }
        }
    }

    private fun moveRope(
        currentPositions: List<KnotPosition>,
        move: Move,
        knots: Int = 2
    ) : List<KnotPosition> {
        return buildList {
            val currentFirstKnotPosition = currentPositions.last { it.index == 0 }
            repeat(move.steps) { step ->
                repeat(knots) { index ->
                    if (index == 0) {
                        add(currentFirstKnotPosition.move(direction = move.direction, step = step + 1))
                    } else {
                        val currentKnotPosition = this.lastOrNull { it.index == index } ?: currentPositions.last { it.index == index }
                        val newKnotBeforePosition = this.last { it.index == index - 1 }
                        add(knotPosition(currentKnotPosition, newKnotBeforePosition))
                    }
                }
            }
        }
    }

    private fun knotPosition(
        currentKnotPosition: KnotPosition,
        knotToTouch: KnotPosition
    ) : KnotPosition {
        val touching = currentKnotPosition.isTouching(knotToTouch)

        return if (touching) {
            currentKnotPosition
        } else {
            currentKnotPosition.determineMove(knotToTouch)
        }
    }

    private fun initialPositions(count: Int = 2) = buildList {
        repeat(count) {
            add(KnotPosition(
                x = 0,
                y = 0,
                index = it
            ))
        }
    }

    private fun moves(input: List<String>) : List<Move> {
        return input.map { line ->
            val split = line.split(" ")
            Move(
                direction = split.first().toDirection(),
                steps = split.last().toInt()
            )
        }
    }

    private fun KnotPosition.determineMove(positionToTouch: KnotPosition) : KnotPosition {
        return if (this.x == positionToTouch.x || this.y == positionToTouch.y) {
            straightMoves().first { position -> position.isTouching(positionToTouch) }
        } else {
            diagonalMoves().first { position -> position.isTouching(positionToTouch) }
        }
    }

    private fun KnotPosition.straightMoves() : List<KnotPosition> {
        return Direction.values().map { direction ->
            this.move(direction)
        }
    }

    private fun KnotPosition.diagonalMoves(): List<KnotPosition> {
        return listOf(
            this.move(Left).move(Up),
            this.move(Left).move(Down),
            this.move(Right).move(Up),
            this.move(Right).move(Down)
        )
    }

    private fun KnotPosition.isTouching(other: KnotPosition) : Boolean {
        val xDifference = other.x - this.x
        val yDifference = other.y - this.y
        return (abs(xDifference) <= 1 && abs(yDifference) <= 1)
    }

    private fun KnotPosition.move(direction: Direction, step: Int = 1) : KnotPosition = when (direction) {
        Left -> this.copy(x = this.x - step)
        Right -> this.copy(x = this.x + step)
        Up -> this.copy(y = this.y + step)
        Down -> this.copy(y = this.y - step)
    }

    private fun String.toDirection() :Direction = when (this) {
        "R" -> Right
        "L" -> Left
        "U" -> Up
        "D" -> Down
        else -> throw RuntimeException()
    }
}

data class KnotPosition(
    val x: Int,
    val y: Int,
    val index : Int
)

enum class Direction {
    Left, Right, Up, Down
}

data class Move(
    val direction: Direction,
    val steps: Int
)