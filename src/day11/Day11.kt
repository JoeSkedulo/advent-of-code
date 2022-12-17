package day11

import Runner
import day11.Operator.*

fun main() {
    Day11Runner().solve()
}

class Day11Runner : Runner<Long>(
    day = 11,
    expectedPartOneTestAnswer = 10605,
    expectedPartTwoTestAnswer = 2713310158
) {

    override fun partOne(input: List<String>, test: Boolean): Long {
        return solve(
            monkeys = monkeys(input),
            rounds = 20,
            worryReduction = 3
        )
    }

    override fun partTwo(input: List<String>, test: Boolean): Long {
        return solve(
            monkeys = monkeys(input),
            rounds = 10000,
            worryReduction = 1
        )
    }

    private fun solve(monkeys: List<Monkey>, rounds: Int, worryReduction: Int) : Long {
        val commonDenominator = monkeys.map { it.test.divisibleBy }
            .reduce { a, b -> a * b }
        repeat(rounds) {
            monkeys.forEach { monkey ->
                monkey.currentItems.forEach { item ->
                    val operationResult = monkey.operation.calculate(item) / worryReduction
                    val testResult = operationResult % monkey.test.divisibleBy == 0L
                    if (testResult) {
                        monkeys[monkey.test.monkeyIfTrue].currentItems.add(operationResult % commonDenominator)
                    } else {
                        monkeys[monkey.test.monkeyIfFalse].currentItems.add(operationResult % commonDenominator)
                    }
                }
                monkey.itemsInspected.addAll(monkey.currentItems)
                monkey.currentItems.clear()
            }
        }
        return monkeys
            .map { monkey -> monkey.itemsInspected.count().toLong() }
            .sortedDescending()
            .subList(0, 2)
            .reduce { a, b -> a * b }
    }

    private fun monkeys(input: List<String>) : List<Monkey> {
        return input.windowed(6, 7).map { lines ->
            monkey(lines)
        }
    }

    private fun monkey(input: List<String>) : Monkey {
        return Monkey(
            currentItems = items(input[1]),
            operation = operation(input[2]),
            test = test(input[3], input[4], input[5])
        )
    }

    private fun items(line: String): MutableList<Long> {
        return line.replace("  Starting items: ", "")
            .split(", ")
            .map { it.toLong() }
            .toMutableList()
    }

    private fun operation(line: String) : Operation {
        val operator = when (line[23]) {
            '*' -> Multiply
            '+' -> Add
            '-' -> Subtract
            else -> throw RuntimeException()
        }
        val value = line.split(" ").last()
            .toLongOrNull()
            ?.let { OperationLong(it) }
            ?: OperationOld
        return Operation(
            operator = operator,
            value = value
        )
    }

    private fun test(line: String, trueLine: String, falseLine: String) : Test {
        return Test(
            divisibleBy = line.split(" ").last().toLong(),
            monkeyIfTrue = trueLine.split( " ").last().toInt(),
            monkeyIfFalse = falseLine.split(" ").last().toInt()
        )
    }

    private fun Operation.calculate(input: Long) : Long {
        val value = when (this.value) {
            is OperationLong -> this.value.v
            OperationOld -> input
        }
        return when (this.operator) {
            Add -> input + value
            Subtract -> input - value
            Multiply -> input * value
        }
    }
}

data class Monkey(
    val currentItems: MutableList<Long>,
    val itemsInspected: MutableList<Long> = mutableListOf(),
    val operation: Operation,
    val test: Test
)

data class Operation(
    val operator: Operator,
    val value: OperationValue
)

data class Test(
    val divisibleBy: Long,
    val monkeyIfTrue: Int,
    val monkeyIfFalse: Int
)

sealed interface OperationValue

data class OperationLong(
    val v: Long
) : OperationValue

object OperationOld : OperationValue

enum class Operator {
    Add, Subtract, Multiply
}