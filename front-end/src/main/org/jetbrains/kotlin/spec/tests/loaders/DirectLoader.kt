package org.jetbrains.kotlin.spec.tests.loaders

import org.jetbrains.kotlin.spec.tests.TestsCoverageColorsArranger.testTypes
import org.jetbrains.kotlin.spec.tests.loaders.GithubTestsLoader.Companion.loadFileFromRawGithub
import org.jetbrains.kotlin.spec.utils.format
import kotlin.js.Promise


class DirectLoader: GithubTestsLoader {
    private val testFileContents = mutableListOf<Map<String, String>>()
    private var promisesQueueLength = 0

    private fun tryRequestRun(testNumber: Int, pathPrefix: String, resolve: (Promise<Array<Map<String, String>>>) -> Unit) {
        promisesQueueLength++
        loadFileFromRawGithub("$pathPrefix.$testNumber.kt").then { response: Map<String, String> ->
            promisesQueueLength--
            testFileContents.add(response)
            tryRequestRun(testNumber + 1, pathPrefix, resolve)
        }.catch {
            promisesQueueLength--
            if (promisesQueueLength == 0) {
                Promise.resolve(testFileContents)
            }
        }
    }

    private fun runSentenceRequests(sentenceCount: Int, pathPrefix: String, resolve: (Promise<Array<Map<String, String>>>) -> Unit) {
        for (i in 0..sentenceCount) {
            testTypes.forEach { testType ->
                tryRequestRun(1, (pathPrefix + (i + 1)).format(testType), resolve)
            }
        }
    }

    override fun loadTestFiles(sectionName: String, sectionsPath: List<String>, paragraphsInfo: List<Map<String, Any>>): Promise<Promise<Array<out Map<String, String>>>> {
        val pathPrefix1 = sectionsPath.joinToString("/") + "/" + sectionName + "/p-"
        return Promise<Promise<Array<Map<String, String>>>> { resolve, _ ->
            paragraphsInfo.forEachIndexed { paragraphsIndex, paragraphInfo ->
                (paragraphInfo["sentenceCount"] as? String)?.toInt()?.let { sentenceCount ->
                    runSentenceRequests(sentenceCount, pathPrefix1 + (paragraphsIndex + 1) + "/{1}/", resolve)
                }
            }
        }
    }
}