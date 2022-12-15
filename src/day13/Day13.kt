package day13

import Runner

fun main() {
    Day13Runner().solve()
}

class Day13Runner : Runner<Int>(
    day = 13,
    expectedPartOneTestAnswer = 13,
    expectedPartTwoTestAnswer = 140
) {

    override fun partOne(input: List<String>): Int {
        return packetPairs(input)
            .map { (left, right) -> left.compareTo(right) }
            .mapIndexedNotNull { index, comparison -> if (comparison == -1) { index + 1 } else { null }  }
            .sum()
    }


    override fun partTwo(input: List<String>): Int {
        val dividerPackets = listOf("[[2]]", "[[6]]").map { s -> s.toPacket() }
        return (packetPairs(input).flatten() + dividerPackets)
            .sortedWith { a, b -> a.compareTo(b) }
            .let { sorted ->
                (sorted.indexOf(dividerPackets.first()) + 1) * (sorted.indexOf(dividerPackets.last()) + 1)
            }
    }

    private fun packetPairs(input: List<String>) : List<PacketPair> {
        return input.windowed(2, 3).map { lines ->
            PacketPair(
                left = lines.first().toPacket(),
                right = lines.last().toPacket()
            )
        }
    }

    private fun List<PacketPair>.flatten() : List<Packet> {
        return flatMap { (left, right) ->
            listOf(left, right)
        }
    }

    private fun Packet.compareTo(other: Packet): Int = let { source ->

        fun ListPacket.compareTo(other: ListPacket) : Int = let { source ->
            source.packets.zip(other.packets).forEach { (a, b) ->
                val c = a.compareTo(b)
                if (c != 0) {
                    return c
                }
            }
            return when {
                source.packets.size == other.packets.size -> 0
                source.packets.size < other.packets.size -> -1
                else -> 1
            }
        }

        return when {
            source is IntPacket && other is IntPacket -> source.int.compareTo(other.int)
            source is ListPacket && other is ListPacket -> source.compareTo(other)
            source is ListPacket && other is IntPacket -> source.compareTo(ListPacket(listOf(other)))
            source is IntPacket && other is ListPacket -> ListPacket(listOf(source)).compareTo(other)
            else -> throw RuntimeException()
        }
    }

    private fun String.toPacket() : Packet {
        val input = removeSurrounding("[", "]")
        return ListPacket(if (input.isEmpty()) {
            emptyList()
        } else {
            buildList {
                var inputIndex = 0
                while (inputIndex <= input.lastIndex) {
                    inputIndex = when (input[inputIndex]) {
                        '[' -> {
                            val closingIndex = input.closingIndex(inputIndex)
                            add(input.substring(inputIndex, closingIndex + 1).toPacket())
                            closingIndex + 1
                        }
                        else -> {
                            val nextComma = input.nextCommaIndex(inputIndex)
                            add(IntPacket(input.substring(inputIndex, nextComma).toInt()))
                            nextComma
                        }
                    }
                    inputIndex++
                }
            }
        })
    }

    private fun String.nextCommaIndex(startIndex: Int) : Int {
        return when (val commaIndex = indexOf(",", startIndex = startIndex + 1)) {
            -1 -> lastIndex + 1
            else -> commaIndex
        }
    }

    private fun String.closingIndex(startIndex: Int) : Int {
        return substring(startIndex + 1).indexOfClosingBrace() +
                substring(0, startIndex).count()
    }

    private fun String.indexOfClosingBrace() : Int {
        val stack = ArrayDeque<Char>()
        var index = 0
        stack.add('[')
        while (stack.isNotEmpty()) {
            when (val c = get(index)) {
                '[' -> stack.add(c)
                ']' -> stack.removeLast()
            }
            index++
        }
        return index
    }

}

data class PacketPair(
    val left: Packet,
    val right: Packet
)

sealed interface Packet

data class ListPacket(
    val packets : List<Packet> = emptyList()
) : Packet {
    override fun toString(): String {
        return packets.toString()
    }
}

data class IntPacket(
    val int: Int
) : Packet {
    override fun toString(): String {
        return int.toString()
    }
}