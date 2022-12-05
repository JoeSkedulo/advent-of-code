abstract class Runner<T>(
    val day: Int,
    val expectedPartOneTestAnswer: T? = null,
    val expectedPartTwoTestAnswer: T? = null
) {

    abstract fun partOne(input : List<String>) : T

    abstract fun partTwo(input: List<String>) : T

    fun solve() {
        test()
        val input = readInput(filename())
        println("Part one answer: ${partOne(input)}")
        println("Part two answer: ${partTwo(input)}")
    }

    private fun test() {
        expectedPartOneTestAnswer?.also { expectedAnswer ->
            val answer = partOne(input = readInput(testFilename()))
            if (answer != expectedAnswer) {
                throw AssertionError("Part one test failed, actual: $answer, expected: $expectedAnswer")
            }
        }
        expectedPartTwoTestAnswer?.also { expectedAnswer ->
            val answer = partTwo(input = readInput(testFilename()))
            if(answer != expectedAnswer) {
                throw AssertionError("Part two test failed, actual: $answer, expected: $expectedAnswer")
            }
        }
    }

    private fun filename() = "day$day/Day$day"

    private fun testFilename() = filename() + "Test"
}