package org.appga.junit5watcher

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test

@ExtendWith(BenchmarkTestExtension::class)
@ExtendWith(BenchmarkExtension::class)
internal class AfterEachTest {

    @AfterEach
    fun afterEach() {
        Thread.sleep(100)
    }

    @Test
    fun test1() {
        Thread.sleep(100)
    }

    companion object {
        @TestFinalization
        @JvmStatic
        fun validateResults(metrics: Metrics, testClassCounters: TestClassCounters) {
            val className = AfterEachTest::class.qualifiedName!!
            val classMetrics = metrics.getResults().first { it.first == className }.second

            assertThat(classMetrics, afterEach = 100.0, tests = 100.0)

            Assertions.assertThat(testClassCounters.testCounter.get()).isEqualTo(1)
        }
    }

}