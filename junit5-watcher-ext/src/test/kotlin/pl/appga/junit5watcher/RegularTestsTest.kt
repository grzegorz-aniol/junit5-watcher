package pl.appga.junit5watcher

import org.assertj.core.api.Assertions
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.time.DurationUnit

@ExtendWith(BenchmarkTestExtension::class)
@ExtendWith(BenchmarkExtension::class)
internal class RegularTestsTest {

    fun test1() {
        Thread.sleep(100)
    }

    fun test2() {
        Thread.sleep(100)
    }

    fun test3() {
        Thread.sleep(100)
    }

    companion object {

        @TestFinalization
        @JvmStatic
        fun validateResults(metrics: Metrics, testClassCounters: TestClassCounters) {
            val className = RegularTestsTest::class.qualifiedName!!
            val classMetrics = metrics.getResults().first { it.first == className }.second

            assertThat(classMetrics, tests = 300.0)

            Assertions.assertThat(testClassCounters.testCounter.get()).isEqualTo(3)
        }
    }

}