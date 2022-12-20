package day18

import Runner

fun main() {
    Day18Runner().solve()
}

class Day18Runner : Runner<Int>(
    day = 18,
    expectedPartOneTestAnswer = 64,
    expectedPartTwoTestAnswer = 58
) {

    override fun partOne(input: List<String>, test: Boolean): Int {
        val cubes = cubes(input)
        return cubes.map { cube -> cube.adjacentCoords() }
            .sumOf { adjacent -> adjacent.count { adjacentCoord -> !cubes.contains(adjacentCoord) } }
    }

    override fun partTwo(input: List<String>, test: Boolean): Int {
        val cubes = cubes(input)
        val external = cubes.calculateExternal()
        return cubes.map { cube -> cube.adjacentCoords() }
            .sumOf { adjacent -> adjacent.count { adjacentCoord -> !cubes.contains(adjacentCoord) && external.contains(adjacentCoord) } }
    }

    private fun List<Coord>.calculateExternal() : Set<Coord> {

        val xOuter = (minOf { coord -> coord.x } - 1 .. maxOf { coord -> coord.x } + 1)
        val yOuter = (minOf { coord -> coord.y } - 1 .. maxOf { coord -> coord.y } + 1)
        val zOuter = (minOf { coord -> coord.z } - 1 .. maxOf { coord -> coord.z } + 1)

        fun recurse(outers: Set<Coord>) : Set<Coord> {
            val adjacentOuters = outers.flatMap { outer -> outer.adjacentOuter(
                cubes = this,
                xOuter = xOuter,
                yOuter = yOuter,
                zOuter = zOuter
            ) }.toSet()
            return outers
                .takeIf { it.size == adjacentOuters.size }
                ?: recurse(adjacentOuters)
        }

        return recurse(initial(
            xOuter = xOuter,
            yOuter = yOuter,
            zOuter = zOuter
        ).toSet())
    }

    private fun Coord.adjacentOuter(
        cubes: List<Coord>,
        xOuter : IntRange,
        yOuter : IntRange,
        zOuter : IntRange,
    ) : Set<Coord> {
        return (adjacentCoords() + this).filter { coord ->
            coord.x in xOuter && coord.y in yOuter && coord.z in zOuter && !cubes.contains(coord)
        }.toSet()
    }


    private fun initial(
        xOuter : IntRange,
        yOuter : IntRange,
        zOuter : IntRange,
    ): List<Coord> {
        return listOf(
            xOuter.flatMap { x -> yOuter.map { y -> Coord(x, y, zOuter.first) } },
            xOuter.flatMap { x -> yOuter.map { y -> Coord(x, y, zOuter.last) } },
            xOuter.flatMap { x -> zOuter.map { z -> Coord(x, yOuter.first, z) } },
            xOuter.flatMap { x -> zOuter.map { z -> Coord(x, yOuter.last, z) } },
            yOuter.flatMap { y -> zOuter.map { z -> Coord(zOuter.first, y, z) } },
            yOuter.flatMap { y -> zOuter.map { z -> Coord(zOuter.last, y, z) } },
        ).flatten()
    }

    private fun Coord.adjacentCoords() : List<Coord> {
        return listOf(
            copy(x = x + 1),
            copy(x = x - 1),
            copy(y = y + 1),
            copy(y = y - 1),
            copy(z = z + 1),
            copy(z = z - 1),
        )
    }

    private fun cubes(input: List<String>) : List<Coord> {
        return input.map { line ->
            val split = line.split(",").map { s -> s.toInt() }
            Coord(
                x = split[0],
                y = split[1],
                z = split[2]
            )
        }
    }
}

data class Coord(
    val x: Int,
    val y: Int,
    val z: Int
)