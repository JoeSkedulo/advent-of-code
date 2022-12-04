abstract class Runner(
    val day: Int,
    val expectedPartOneTestAnswer: Int? = null,
    val expectedPartTwoTestAnswer: Int? = null
) {

    abstract fun partOne(input : List<String>) : Int

    abstract fun partTwo(input: List<String>) : Int

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