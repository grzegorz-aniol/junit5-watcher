package org.appga.junit5watcher

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage.withPercentage
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.time.DurationUnit

@ExtendWith(BenchmarkTestExtension::class)
@ExtendWith(BenchmarkExtension::class)
internal class RepeatedTestTest {

    @RepeatedTest(3)
    fun repeatedTest() {
        Thread.sleep(80)
    }

    companion object {
        @JvmStatic
        @AfterAll
        fun validateResults(metrics: Metrics, testClassCounters: TestClassCounters) {
            val className = RepeatedTestTest::class.qualifiedName!!
            val classMetrics = metrics.getResults().first { it.first == className }.second

            assertThat(classMetrics,
                tests = 240.0,
                skipBeforeEach = true // do not validate 'before each' time as @ParameterizedTest extension run its own
            )

            assertThat(testClassCounters.testCounter.get()).isEqualTo(3)
        }
    }

}
