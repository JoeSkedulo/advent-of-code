package day16

import Runner

fun main() {
    Day16Runner().solve()
}

class Day16Runner : Runner<Int>(
    day = 16,
    expectedPartOneTestAnswer = 1651,
    expectedPartTwoTestAnswer = 1707
) {

    override fun partOne(input: List<String>, test: Boolean): Int {
        val valveInput = valves(input)
        return recurseValves(
            initialValve = valveInput
                .first { valve -> valve.label == "AA" }
                .let { valve -> Valve(label = valve.label, flowRate = valve.flowRate) },
            paths = getPaths(valveInput),
            maxTime = 30
        )
    }

    override fun partTwo(input: List<String>, test: Boolean): Int {
        val valveInput = valves(input)
        return recurseValves(
            initialValve = valveInput
                .first { valve -> valve.label == "AA" }
                .let { valve -> Valve(label = valve.label, flowRate = valve.flowRate) },
            paths = getPaths(valveInput),
            maxTime = 26,
            helper = true
        )
    }

    private fun recurseValves(
        initialValve: Valve,
        paths: Map<Valve, Map<Valve, Int>>,
        maxTime: Int,
        helper: Boolean = false
    ) : Int {

        fun openValves(
            valve: Valve,
            paths: Map<Valve, Map<Valve, Int>>,
            visited: Set<Valve> = setOf(),
            scores: MutableList<Int> = mutableListOf(),
            currentTime: Int = 0,
            maxTime: Int,
            currentScore: Int = 0,
            helper: Boolean = false
        ) : List<Int> {
            scores.add(currentScore)
            paths[valve]!!.forEach { (pathNode, travelTime) ->
                val canVisit = canVisitNode(
                    valve = pathNode,
                    visited = visited,
                    travelTime = travelTime,
                    currentTime = currentTime,
                    maxTime = maxTime
                )
                if (canVisit) {
                    val updatedScore = calculateScore(
                        valve = pathNode,
                        currentScore = currentScore,
                        travelTime = travelTime,
                        currentTime = currentTime,
                        maxTime = maxTime,
                    )
                    openValves(
                        valve = pathNode,
                        paths = paths,
                        visited = visited.toMutableSet().apply { add(pathNode) },
                        scores = scores,
                        currentTime = currentTime + travelTime + 1,
                        maxTime = maxTime,
                        currentScore = updatedScore,
                        helper = helper
                    )
                }
            }
            if (helper) {
                openValves(
                    currentScore = currentScore,
                    valve = initialValve,
                    paths = paths,
                    visited = visited,
                    scores = scores,
                    maxTime = maxTime
                )
            }
            return scores
        }

        return openValves(
            valve = initialValve,
            paths = paths,
            maxTime = maxTime,
            helper = helper
        ).max()
    }

    private fun canVisitNode(
        valve: Valve,
        visited: Set<Valve>,
        travelTime: Int,
        currentTime: Int,
        maxTime: Int
    ) : Boolean {
        return !visited.contains(valve) && currentTime + travelTime + 1 < maxTime
    }

    private fun calculateScore(
        valve: Valve,
        currentScore: Int,
        travelTime: Int,
        currentTime: Int,
        maxTime: Int
    ) : Int {
        return currentScore + (maxTime - currentTime - travelTime - 1) * valve.flowRate
    }

    // https://www.programiz.com/dsa/floyd-warshall-algorithm
    fun getPaths(valves: List<ValveInput>): Map<Valve, MutableMap<Valve, Int>> {
        val paths = buildMap {
            valves.forEach { valve ->
                put(Valve(valve.label, valve.flowRate), buildMap {
                    valve.leadsTo.forEach { leadTo ->
                        put(Valve(leadTo, valves.first { valve -> valve.label == leadTo }.flowRate), 1)
                    }
                }.toMutableMap())
            }
        }

        paths.keys.forEach { i ->
            paths.keys.forEach { j ->
                paths.keys.forEach { k ->
                    val ji = paths.get(j)?.get(i) ?: 9999
                    val ik = paths.get(i)?.get(k) ?: 9999
                    val jk = paths.get(j)?.get(k) ?: 9999
                    if (ji + ik < jk) {
                        paths.get(j)?.set(k, ji + ik)
                  }
                }
            }
        }

        paths.forEach { (_, path) ->
            val keep = path.filterKeys { key -> valves.first { valve -> valve.label == key.label }.flowRate > 0 }
            path.clear()
            path.putAll(keep)
        }

        return paths
    }

    private fun valves(input: List<String>) : List<ValveInput> {
        return input.map { line ->
            val (valve, tunnels) = line.split(";")
            val label = valve.substring(6, 8)
            val flowRate = valve.substring(valve.indexOf('=') + 1).toInt()
            val leadsTo = tunnels.filter { c -> c.isUpperCase() || c == ',' }.split(",")
            ValveInput(
                label = label,
                flowRate = flowRate,
                leadsTo = leadsTo
            )
        }
    }
}

data class ValveInput(
    val label: String,
    val leadsTo: List<String>,
    val flowRate: Int
)

data class Valve(
    val label: String,
    val flowRate: Int
) {
    override fun toString(): String {
        return label
    }
}

