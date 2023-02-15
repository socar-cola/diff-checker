package kr.cola.diff.checker.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.flipkart.zjsonpatch.JsonDiff
import com.google.gson.JsonParser
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.apache.tools.ant.DirectoryScanner
import java.io.FileReader

fun <T> println(msg: T) {
    kotlin.io.println("[${Thread.currentThread().name}] $msg")
}

class DiffChecker(
    private val baseDir: String,
) {

    fun run() = runBlocking {
        val dirScanner = DirectoryScanner()
        dirScanner.setIncludes(arrayOf("**/http/response"))
        dirScanner.setBasedir(baseDir)
        dirScanner.isCaseSensitive = false

        dirScanner.scan()

        val dirDefList = mutableListOf<Deferred<Unit>>()

        dirScanner.includedDirectories.map { dir ->
            val dirDef = async(Dispatchers.Default) {
                val fileScanner = DirectoryScanner()

                fileScanner.setIncludes(arrayOf("**/*.json"))
                fileScanner.setBasedir(dir)
                fileScanner.isCaseSensitive = false

                fileScanner.scan()

                val files = fileScanner.includedFiles

                val parser = JsonParser()

                val errors = mutableListOf<JsonNode>()
                val errorFileNames = mutableListOf<String>()

                val fileDefList = mutableListOf<Deferred<Unit>>()

                for (i in 0..files.size step 2) {
                    if (i + 1 >= files.size) {
                        break
                    }

                    val fileDef = async(Dispatchers.Default) {
                        val file1 = parser.parse(FileReader("$baseDir/${dir}/${files[i]}"))
                        val file2 = parser.parse(FileReader("$baseDir/${dir}/${files[i+1]}"))

                        val tree1 = jacksonObjectMapper().readTree(file1.toString())
                        val tree2 = jacksonObjectMapper().readTree(file2.toString())

                        val response = JsonDiff.asJson(tree1, tree2)

                        if (response.size() != 0) {
                            errors.add(response)
                            errorFileNames.addAll(listOf(files[i], files[i+1]))
                        }
                    }

                    fileDefList.add(fileDef)
                }

                fileDefList.awaitAll()

                println("========================================================================================================================================================================")
                println("폴더이름: $dir")
                if (errors.isEmpty()) {
                    println("모든 결과는 동일합니다.")
                } else {
                    println("결과가 동일하지 않은 응답이 존재합니다. $errorFileNames")
                }
            }

            dirDefList.add(dirDef)
        }

        dirDefList.awaitAll()
    }
}
