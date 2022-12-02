package day2

import day2.Move.*
import day2.Part.PartOne
import day2.Part.PartTwo
import day2.RoundResult.*
import readInput

fun main() {

    fun rounds(input: List<String>) : List<Round> {
        return input.map { line -> Round(
            opponentInput = Move.values().first { it.v == line.first() },
            input = Input.values().first { it.v == line.last() },
        ) }
    }

    fun part1(input: List<String>): Int {
        return rounds(input).sumOf { round -> round.toScore(PartOne) }
    }

    fun part2(input: List<String>): Int {
        return rounds(input).sumOf { round -> round.toScore(PartTwo) }
    }

    val input = readInput("day2/Day02")
    println(part1(input))
    println(part2(input))
}

enum class Move(val v: Char, val score: Int) {
    Rock('A', 1),
    Paper('B', 2),
    Scissors('C', 3)
}

enum class Input(
    val v: Char,
    val partOne: Move,
    val partTwo: RoundResult
) {
    X('X', Rock, Loss),
    Y('Y', Paper, Draw),
    Z('Z', Scissors, Win)
}

enum class RoundResult(val score: Int) {
    Win(6), Loss(0), Draw(3)
}

enum class Part {
    PartOne, PartTwo
}

data class Round(
    val opponentInput: Move,
    val input: Input
)

fun Round.toMove(part: Part) = when(part) {
    PartOne -> input.partOne
    PartTwo -> when(input.partTwo) {
        Loss -> opponentInput.toLoss()
        Draw -> opponentInput.toDraw()
        Win -> opponentInput.toWin()
    }
}

fun Move.toWin() = when (this) {
    Rock -> Paper
    Paper -> Scissors
    Scissors -> Rock
}

fun Move.toLoss() = when (this) {
    Rock -> Scissors
    Paper -> Rock
    Scissors -> Paper
}

fun Move.toDraw() = this

fun Round.toResultScore(f : (Round) -> Move) = when {
    opponentInput == f(this) -> Draw
    opponentInput == Rock && f(this) == Paper -> Win
    opponentInput == Paper && f(this) == Scissors -> Win
    opponentInput == Scissors && f(this) == Rock -> Win
    else -> Loss
}.score

fun Round.toScore(part: Part) = toMove(part).let { move -> move.score + toResultScore { move } }