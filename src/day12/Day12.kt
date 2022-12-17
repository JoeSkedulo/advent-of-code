package day12

import Runner
import java.util.*
import kotlin.collections.HashSet

fun main() {
    Day12Runner().solve()
}

class Day12Runner : Runner<Int>(
    day = 12,
    expectedPartOneTestAnswer = 31,
    expectedPartTwoTestAnswer = 29
) {

    override fun partOne(input: List<String>, test: Boolean): Int {
        val squares = mapSquares(input)
        val end = squares.first { it.elevation == "E" }
        val start = squares.first { it.elevation == "S" }

        return search(
            squares = squares,
            start = start,
            end = end
        ) ?: throw RuntimeException()
    }

    override fun partTwo(input: List<String>, test: Boolean): Int {
        val squares = mapSquares(input)
        val end = squares.first { it.elevation == "E" }
        val starts = squares.filter { it.getElevationInt() == 0 }
        val possibleRoutes = starts.mapNotNull { start ->
            search(
                squares = squares,
                start = start,
                end = end
            )
        }
        return possibleRoutes.min()
    }

    private fun mapSquares(input: List<String>) : List<MapSquare> {
        return input.mapIndexed { x, line ->
            line.mapIndexed { y, char ->
                MapSquare(
                    x = x,
                    y = y,
                    elevation = char.toString()
                )
            }
        }.flatten()
    }

    private fun search(squares: List<MapSquare>, start: MapSquare, end: MapSquare) : Int? {
        val queue = LinkedList<Pair<MapSquare, Int>>()
        val seen = HashSet<MapSquare>()

        queue.add(Pair(start, 0))

        while (!queue.isEmpty()) {
            val (square, distance) = queue.remove()

            if (square == end) {
                return distance
            }

            if (!seen.contains(square)) {
                seen.add(square)
                squares.adjacentSquares(square).forEach { adjacentSquare ->
                    if (!seen.contains(adjacentSquare)) {
                        queue.add(Pair(adjacentSquare, distance + 1))
                    }
                }
            }
        }

        return null
    }

    private fun List<MapSquare>.getOrNull(x: Int, y: Int) : MapSquare? {
        return firstOrNull { square -> square.x == x && square.y == y }
    }

    private fun List<MapSquare>.adjacentSquares(currentSquare: MapSquare) : List<MapSquare> {
        return listOfNotNull(
            getOrNull(currentSquare.x - 1, currentSquare.y),
            getOrNull(currentSquare.x, currentSquare.y - 1),
            getOrNull(currentSquare.x, currentSquare.y + 1),
            getOrNull(currentSquare.x + 1, currentSquare.y),
        ).filter { adjacent ->
            currentSquare.canMoveTo(adjacent)
        }
    }

    private fun MapSquare.canMoveTo(other: MapSquare) : Boolean = let { current ->
        val currentValue = current.getElevationInt()
        val otherValue = other.getElevationInt()
        otherValue -1 <= currentValue
    }

    private fun MapSquare.getElevationInt() = let { square ->
        when (val elevation = square.elevation) {
            "S" -> 0
            "E" -> 25
            else -> ('a'..'z').map { it.toString() }.indexOf(elevation)
        }
    }

}

data class MapSquare(
    val x: Int,
    val y: Int,
    val elevation: String
)