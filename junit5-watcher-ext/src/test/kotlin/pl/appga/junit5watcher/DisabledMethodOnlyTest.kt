package pl.appga.junit5watcher

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(BenchmarkTestExtension::class)
@ExtendWith(BenchmarkExtension::class)
internal class DisabledMethodOnlyTest {

    @Disabled
    @Test
    fun disabledMethodTest() {
        // do nothing
    }

    companion object {
        @TestFinalization
        @JvmStatic
        fun validateResults(metrics: Metrics, testClassCounters: TestClassCounters) {
            val className = DisabledMethodOnlyTest::class.qualifiedName!!
            val resultsMetrics = metrics.getResults().toMap()

            assertThat(testClassCounters.testCounter.get()).isEqualTo(0)

            assertThat(resultsMetrics)
                .`as`("Class is included in metrics")
                .containsOnlyKeys(className)

            assertThat(resultsMetrics[className])
                .`as`("Only cumulative metric for class is included in metrics")
                .containsOnlyKeys(MetricType.CUMULATIVE)
        }
    }
}