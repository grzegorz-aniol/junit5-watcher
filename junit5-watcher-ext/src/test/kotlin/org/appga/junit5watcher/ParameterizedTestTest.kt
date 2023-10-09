package org.appga.junit5watcher

import org.assertj.core.api.Assertions
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.time.DurationUnit

@ExtendWith(BenchmarkTestExtension::class)
@ExtendWith(BenchmarkExtension::class)
internal class ParameterizedTestTest {

    @ParameterizedTest
    @ValueSource(strings = ["abc", "123", "xyz", "___"])
    fun parameterized(param: String) {
        Thread.sleep(100)
    }

    companion object {
        @JvmStatic
        @AfterAll
        fun validateResults(metrics: Metrics, testClassCounters: TestClassCounters) {
            val className = ParameterizedTestTest::class.qualifiedName!!
            val classMetrics = metrics.getResults().first { it.first == className }.second

            assertThat(classMetrics,
                tests = 400.0,
                skipBeforeEach = true // do not validate 'before each' time as @ParameterizedTest extension run its own
            )

            Assertions.assertThat(testClassCounters.testCounter.get()).isEqualTo(4)
        }
    }

}