package day7

import Runner

fun main() {
    Day7Runner().solve()
}

class Day7Runner : Runner<Int>(
    day = 7,
    expectedPartOneTestAnswer = 95437,
    expectedPartTwoTestAnswer = 24933642
) {

    override fun partOne(input: List<String>): Int {
        val contents = directoryContents(input)
        val directories = sizedDirectories(contents)
        return directories
            .filter { directory -> directory.size <= 100000 }
            .sumOf { directory -> directory.size }
    }

    override fun partTwo(input: List<String>): Int {
        val contents = directoryContents(input)
        val directories = sizedDirectories(contents)
        val totalSize = directories.maxOf { directory ->  directory.size }
        val freeSpace = 70000000 - totalSize
        val needed = 30000000 - freeSpace
        return directories
            .filter { directory -> directory.size >= needed }
            .minOf { directory -> directory.size }
    }

    private fun sizedDirectories(files: List<DirectoryContent>) : List<SizedDirectory> {
        val paths = files.map { file -> file.path }.distinct()
        return paths.map { path ->
            SizedDirectory(
                fullPath = path,
                size = files
                    .filter { content -> content.path.startsWith(path) }
                    .filterIsInstance<File>()
                    .sumOf { it.size }
            )
        }
    }

    private fun directoryContents(input: List<String>) : List<DirectoryContent> {
        return input.mapIndexedNotNull { index, line ->
            when {
                line.isFile() -> File(size = line.fileSize(), path = input.pathForLine(index))
                line.isDir() -> Directory(path = input.pathForLine(index))
                else -> null
            }
        }
    }

    private fun List<String>.pathForLine(number: Int) : String = let { lines ->
        return buildList {
            lines.subList(0, number).forEach { line ->
                when {
                    line.isBack() -> removeLast()
                    line.isChangeDirectory() -> add(line.dirName())
                    line.isRootDirectory() -> clear()
                }
            }
        }.joinToString("/")
    }

    private fun String.fileSize() = split(" ")[0].toInt()

    private fun String.dirName() = split(" ")[2]

    private fun String.isFile() = first().toString().toIntOrNull() != null

    private fun String.isDir() = startsWith("dir")

    private fun String.isChangeDirectory() = split(" ")[1] == "cd"

    private fun String.isBack() = this == "$ cd .."

    private fun String.isRootDirectory() = this == "$ cd /"
}

sealed class DirectoryContent(
    open val path: String
)

data class File(
    val size: Int,
    override val path: String
) : DirectoryContent(path)

data class Directory(
    override val path: String
) : DirectoryContent(path)

data class SizedDirectory(
    val fullPath: String,
    val size: Int
)
