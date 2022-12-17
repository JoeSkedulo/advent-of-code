package day14

import Runner
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


fun main() {
    Day14Runner().solve()
}

class Day14Runner : Runner<Int>(
    day = 14,
    expectedPartOneTestAnswer = 24,
    expectedPartTwoTestAnswer = 93
) {

    override fun partOne(input: List<String>, test: Boolean): Int {
        val scans = getScans(input)
        val grid = createGrid(scans)
        var sandAdded = 0

        do {
            val freeSpace = grid.calculateFreeSpace()?.also { space ->
                grid.updateSpace(space.toSand())
                sandAdded++
                grid.saveFile(index = sandAdded, width = 500, height = 2300, prefix = "partOne")
            }
        } while (freeSpace != null)

        return sandAdded
    }

    override fun partTwo(input: List<String>, test: Boolean): Int {
        val scans = getScans(input)
        val height = scans.flatMap { scan -> scan.points }.maxOf { point -> point.y }
        val grid = createGrid(
            xMin = 500 - height - 1,
            xMax = 500 + height + 1,
            yMax = height + 1,
            scans = scans
        ).addRockFloor()
        var sandAdded = 0

        do {
            val freeSpace = grid.calculateFreeSpace()?.also { space ->
                grid.updateSpace(space.toSand())
                sandAdded++
                grid.saveFile(index = sandAdded, width = 2550, height = 2300, prefix = "partTwo")
            }
        } while (freeSpace != null)

        return sandAdded
    }

    private fun Grid.calculateFreeSpace(x : Int = 500, y : Int = 0) : Free? {
        val row = value.map { r -> r[x - value.first().first().x] }
        val lowestFree = row.subList(y, row.lastIndex).takeWhile { it is Free }.lastOrNull() ?: return null
        val left = getSpace(x = lowestFree.x - 1, y = lowestFree.y + 1) as? Free
        val right = getSpace(x = lowestFree.x + 1, y = lowestFree.y + 1) as? Free
        return when {
            left == null && right == null -> lowestFree as Free
            left != null -> calculateFreeSpace(x = x - 1, y = lowestFree.y + 1)
            else -> calculateFreeSpace(x = x + 1, y = lowestFree.y + 1)
        }
    }

    private fun createGrid(scans: List<RockScan>) : Grid {
        val points = scans.flatMap { scan -> scan.points }
        return createGrid(
            xMin = points.minOf { point -> point.x } - 1,
            xMax = points.maxOf { point -> point.x } + 1,
            yMax = points.maxOf { point -> point.y } + 1,
            scans = scans
        )
    }

    private fun createGrid(
        xMin : Int,
        xMax: Int,
        yMax : Int,
        scans: List<RockScan>
    ) : Grid {
        return Grid(buildList {
            (0..yMax).forEach { y ->
                add(buildList {
                    (xMin..xMax).forEach { x ->
                        add(Free(x = x, y = y))
                    }
                }.toMutableList())
            }
        }).addRocks(scans)
    }

    private fun Grid.addRocks(scans: List<RockScan>) = apply {
        scans.forEach { scan ->
            repeat(scan.points.size - 1) { index ->
                val start = scan.points[index]
                val end = scan.points[index + 1]
                val xRange = if (start.x >= end.x) {
                    end.x..start.x
                } else {
                    start.x..end.x
                }
                val yRange = if (start.y >= end.y) {
                    end.y..start.y
                } else {
                    start.y..end.y
                }
                xRange.forEach {
                    updateSpace(Rock(x = it, y = start.y))
                }
                yRange.forEach {
                    updateSpace(Rock(x = start.x, y = it))
                }
            }
        }
    }

    private fun Grid.addRockFloor() : Grid = let { grid ->
        val current = grid.value.toMutableList()
        val lastRow = current.last()
        current.add(
            buildList {
                (lastRow.first().x..lastRow.last().x).forEach { x ->
                    add(Rock(x = x, y = current.lastIndex))
                }
            }.toMutableList()
        )
        Grid(current)
    }

    private fun Grid.updateSpace(new: Space) {
        val y = new.y
        val x = new.x - value.first().first().x
        value[y][x] = new
    }

    private fun Grid.getSpace(x: Int, y: Int) : Space? {
        val xIndex = x - value.first().first().x
        return value.getOrNull(y)?.getOrNull(xIndex)
    }

    private fun getScans(input: List<String>) : List<RockScan> {
        return input.map { line -> line.toScan() }
    }

    private fun String.toScan() : RockScan {
        return RockScan(points = split(" -> ")
            .map { s -> s.toPoint() }
        )
    }

    private fun Free.toSand() : Sand {
        return Sand(x = x, y = y)
    }

    private fun String.toPoint() : RockPoint {
        val (x, y) = split(",").map { it.toInt() }
        return RockPoint(
            x = x,
            y = y
        )
    }

    private fun Grid.saveFile(index: Int, width: Int, height: Int, prefix: String) {
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.graphics
        graphics.color = Color(1, 22, 39)
        graphics.fillRect(0, 0, width, height)
        graphics.font = Font("SF Mono", Font.PLAIN, 12)
        toString().split("\n").forEachIndexed { i, line ->
            line.forEachIndexed { j, c ->
                if (c == 'o') {
                    graphics.color = Color.GREEN
                } else {
                    graphics.color = Color.GRAY
                }
                graphics.drawString(c.toString(), 5 + (j * 8), (i + 1) * 14)
            }
        }
        graphics.color = Color.GRAY
        graphics.font = Font("SF Mono", Font.PLAIN, 24)
        graphics.drawString("Sand Count: ",10, 30)
        graphics.color = Color.GREEN
        graphics.drawString(index.toString(), 180, 30)
        ImageIO.write(bufferedImage, "jpg", File("src/day14/img/$prefix-$index.jpg"))
    }

}

data class RockScan(
    val points : List<RockPoint>
)

data class RockPoint(
    val x: Int,
    val y: Int
)

sealed class Space(
    open val x: Int,
    open val y: Int
)

data class Free(
    override val x: Int,
    override val y: Int
) : Space(x = x, y = y) {
    override fun toString(): String = " "
}

data class Rock(
    override val x: Int,
    override val y: Int
) : Space(x = x, y = y) {
    override fun toString(): String = "#"
}

data class Sand(
    override val x: Int,
    override val y: Int
) : Space(x = x, y = y){
    override fun toString(): String = "o"
}

@JvmInline
value class Grid(val value: List<MutableList<Space>>) {
    override fun toString(): String {
        return buildString {
            value.forEach { y ->
                y.forEach { x ->
                    append(x)
                }
                append("\n")
            }
        }
    }
}