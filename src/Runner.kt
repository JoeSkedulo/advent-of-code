abstract class Runner<T>(
    val day: Int,
    val expectedPartOneTestAnswer: T? = null,
    val expectedPartTwoTestAnswer: T? = null
) {

    abstract fun partOne(input: List<String>, test: Boolean) : T

    abstract fun partTwo(input: List<String>, test: Boolean) : T

    fun solve() {
        test()
        val input = readInput(filename())
        println("Part one answer: ${partOne(input, false)}")
        println("Part two answer: ${partTwo(input, false)}")
    }

    private fun test() {
        expectedPartOneTestAnswer?.also { expectedAnswer ->
            val answer = partOne(input = readInput(testFilename()), test = true)
            if (answer != expectedAnswer) {
                throw AssertionError("Part one test failed, actual: $answer, expected: $expectedAnswer")
            } else {
                println("Part one test answer: $answer")
            }
        }
        expectedPartTwoTestAnswer?.also { expectedAnswer ->
            val answer = partTwo(input = readInput(testFilename()), test = true)
            if(answer != expectedAnswer) {
                throw AssertionError("Part two test failed, actual: $answer, expected: $expectedAnswer")
            } else {
                println("Part two test answer: $answer")
            }
        }
    }

    private fun filename() = "day$day/Day$day"

    private fun testFilename() = filename() + "Test"
}