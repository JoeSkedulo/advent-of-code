package day8

import Runner

fun main() {
    Day8Runner().solve()
}

class Day8Runner : Runner<Int>(
    day = 8,
    expectedPartOneTestAnswer = 21,
    expectedPartTwoTestAnswer = 8
) {

    override fun partOne(input: List<String>): Int {
        val rows = rowsOfTrees(input)
        val columns = columnsOfTrees(rows)

        return visibleTreesFromEdge(rows, columns)
            .distinctBy { it.coord }
            .count()
    }

    override fun partTwo(input: List<String>): Int {
        val rows = rowsOfTrees(input)
        val columns = columnsOfTrees(rows)
        val allTress = (columns + rows).flatten().distinctBy { it.coord }
        return allTress.maxOf { tree ->
            scenicScore(
                tree = tree,
                rows = rows,
                columns = columns
            )
        }
    }

    private fun scenicScore(tree: Tree, rows: List<List<Tree>>, columns: List<List<Tree>>) : Int {
        val x = tree.coord.split(":")[0].toInt()
        val y = tree.coord.split(":")[1].toInt()
        val xForward = rows[x].subList(y + 1, rows.size)
        val yForward = columns[y].subList(x + 1, columns.size)
        val xBackward = rows[x].subList(0, y).reversed()
        val yBackward = columns[y].subList(0, x).reversed()
        return xForward.countVisibleTrees(tree.height) *
            yForward.countVisibleTrees(tree.height) *
            xBackward.countVisibleTrees(tree.height) *
            yBackward.countVisibleTrees(tree.height)
    }
    
    private fun List<Tree>.countVisibleTrees(height: Int) : Int = let { trees ->
        when (val first = trees.indexOfFirst { it.height >= height } ) {
            -1 -> trees.count()
            else -> first + 1
        }
    }

    private fun visibleTreesFromEdge(rows: List<List<Tree>>, columns: List<List<Tree>>) : List<Tree> {
        return mapToVisible(rows) +
            mapToVisible(rows, true) +
            mapToVisible(columns) +
            mapToVisible(columns, true)
    }

    private fun mapToVisible(input: List<List<Tree>>, reverse: Boolean = false) : List<Tree> {
        return input.flatMap { row ->
            val r = if (reverse) {
                row.reversed()
            } else {
                row
            }
            r.filterIndexed { index, tree ->
                val subset = r.subList(0, index)
                subset.isEmpty() || tree.height > subset.maxOf { it.height }
            }
        }
    }

    private fun rowsOfTrees(input: List<String>): List<List<Tree>> {
        return input.mapIndexed { i, line ->
            line.mapIndexed { j, char ->
                Tree(
                    height = char.toString().toInt(),
                    coord = "$i:$j"
                )
            }
        }
    }

    private fun columnsOfTrees(rows: List<List<Tree>>) : List<List<Tree>> {
        return buildList {
            repeat (rows.first().count()) { i ->
                add(rows.mapIndexed { j, row ->
                    Tree(
                        height = row[i].height,
                        coord = "$j:$i"
                    )
                })
            }
        }
    }
}

data class Tree(
    val height: Int,
    val coord: String
)
