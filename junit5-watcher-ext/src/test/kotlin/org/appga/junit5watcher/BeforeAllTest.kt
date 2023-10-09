package org.appga.junit5watcher

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test

@ExtendWith(BenchmarkTestExtension::class)
@ExtendWith(BenchmarkExtension::class)
internal class BeforeAllTest {

    @Test
    fun test1() {
        Thread.sleep(100)
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            Thread.sleep(100)
        }

        @AfterAll
        @JvmStatic
        fun validateResults(metrics: Metrics, testClassCounters: TestClassCounters) {
            val className = BeforeAllTest::class.qualifiedName!!
            val classMetrics = metrics.getResults().first { it.first == className }.second

            assertThat(classMetrics, beforeAll = 100.0, tests = 100.0)

            Assertions.assertThat(testClassCounters.testCounter.get()).isEqualTo(1)
        }
    }

}