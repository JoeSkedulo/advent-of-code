package day20

import Runner

fun main() {
    Day20Runner().solve()
}

class Day20Runner : Runner<Long>(
    day = 20,
    expectedPartOneTestAnswer = 3,
    expectedPartTwoTestAnswer = 1623178306
) {

    override fun partOne(input: List<String>, test: Boolean): Long {
        val mutable = mix(input = input, key = 1, times = 1)
            .map { indexLong -> indexLong.long }
        val startIndex = mutable.indexOfFirst { long -> long == 0L }
        return mutable[(1000 + startIndex).mod(mutable.size)] +
                mutable[(2000 + startIndex).mod(mutable.size)] +
                mutable[(3000 + startIndex).mod(mutable.size)]
    }

    override fun partTwo(input: List<String>, test: Boolean): Long {
        val mutable = mix(input = input, key = 811589153, times = 10)
            .map { indexLong -> indexLong.long }
        val startIndex = mutable.indexOfFirst { long -> long == 0L }
        return mutable[(1000 + startIndex).mod(mutable.size)] +
                mutable[(2000 + startIndex).mod(mutable.size)] +
                mutable[(3000 + startIndex).mod(mutable.size)]
    }

    private fun mix(input: List<String>, key: Int = 1, times: Int = 1) : List<IndexLong> {
        val longs = getLongs(input = input, key = key)
        val mixed = longs.toMutableList()
        repeat(times) {
            longs.forEach { int ->
                val currentIndex = mixed.indexOf(int)
                val newIndex = (currentIndex + int.long).mod(mixed.size - 1)
                mixed.removeAt(currentIndex)
                mixed.add(newIndex, int)
            }
        }
        return mixed
    }

    private fun getLongs(input: List<String>, key: Int = 1) : List<IndexLong> {
        return input.mapIndexed { index, line ->
            IndexLong(
                index = index,
                long = line.toLong() * key
            )
        }
    }
}

data class IndexLong(
    val index: Int,
    val long: Long
)

