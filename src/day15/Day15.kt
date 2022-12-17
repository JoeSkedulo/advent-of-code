package day15

import Runner
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day15Runner().solve()
}

class Day15Runner : Runner<Long>(
    day = 15,
    expectedPartOneTestAnswer = 26,
    expectedPartTwoTestAnswer = 56000011
) {

    override fun partOne(input: List<String>, test: Boolean): Long {
        val sensors = sensors(input)
        val sensorsAndBeacons = sensors.flatMap { sensor -> listOf(sensor, sensor.closestBeacon) }.toSet()
        val y = if (test) {
            10L
        } else {
            2000000L
        }
        return ranges(sensors, y = y)
            .merge()
            .sumOf { range -> range.last - range.first + 1  }
            .minus(sensorsAndBeacons.count { it.y == y })
    }

    override fun partTwo(input: List<String>, test: Boolean): Long {
        val sensors = sensors(input)
        val maxCoord = if (test) {
            20L
        } else {
            4000000L
        }
        return signal(candidates(sensors, maxCoord), maxCoord)
    }

    private fun signal(candidates: List<List<LongRange>>, maxCoord: Long) : Long {
        return candidates.flatMapIndexed { y, ranges ->
            ranges.merge()
                .map { range -> range.first + range.last + 1 }
                .map { x -> y to x }
        }.first { (_, x) -> x <= maxCoord }
            .let { (y, x) -> x * 4000000L + y }
    }

    private fun candidates(sensors: List<Sensor>, maxCoord: Long) : List<List<LongRange>> {
        return buildList {
            repeat(maxCoord.toInt()) { y ->
                add(buildList {
                    sensors.forEach { sensor ->
                        val distance = sensor.manhattanDistance() - abs(y - sensor.y)
                        val min = max(0, (sensor.x - distance))
                        val max = min(maxCoord, (sensor.x + distance))
                        if (min <= max) {
                            add(min..max)
                        }
                    }
                })
            }
        }
    }

    private fun ranges(sensors: List<Sensor>, y: Long) : List<LongRange> {
        return buildList {
            sensors.forEach { sensor ->
                val distance = sensor.manhattanDistance() - abs(y - sensor.y)
                if (distance >= 0) {
                    add((sensor.x - distance)..sensor.x + distance)
                }
            }
        }
    }

    private fun List<LongRange>.merge(): List<LongRange> = let {ranges ->
        val sortedRanges = ranges.sortedBy { it.first }
        return buildList {
            var currentRange: LongRange? = null
            for (range in sortedRanges) {
                if (currentRange == null) {
                    currentRange = range
                }

                currentRange = if (range.first <= currentRange.last + 1) {
                    LongRange(currentRange.first, maxOf(currentRange.last, range.last))
                } else {
                    add(currentRange)
                    range
                }
            }

            if (currentRange != null) {
                add(currentRange)
            }
        }
    }

    private fun Sensor.manhattanDistance() : Long {
        return manhattanDistance(
            startX = x,
            startY = y,
            endX = closestBeacon.x,
            endY = closestBeacon.y
        )
    }

    private fun manhattanDistance(startX: Long, startY: Long, endX: Long, endY: Long): Long {
        return abs(endX - startX) + abs(endY - startY)
    }

    private fun sensors(input: List<String>) : List<Sensor> {
        return input.map { line ->
            val (sensor, beacon) = line.split(":")
            Sensor(
                x = sensor.coordValue("x"),
                y = sensor.coordValue("y"),
                closestBeacon = Beacon(
                    x = beacon.coordValue("x"),
                    y = beacon.coordValue("y")
                )
            )
        }
    }

    private fun String.coordValue(coord: String) : Long {
        val subString = substring(indexOf(coord) + 2)
        return if (subString.startsWith("-")) {
            subString.drop(1)
                .takeWhile { c -> c.isDigit() }.toLong() * -1
        } else {
            subString.takeWhile { c -> c.isDigit() }.toLong()
        }
    }
}

interface Coord {
    val x: Long
    val y: Long
}

data class Sensor(
    override val x : Long,
    override val y: Long,
    val closestBeacon: Beacon
) : Coord

data class Beacon(
    override val x : Long,
    override val y: Long
) : Coord