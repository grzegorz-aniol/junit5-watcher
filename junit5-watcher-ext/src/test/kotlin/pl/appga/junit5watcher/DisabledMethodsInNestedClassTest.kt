package pl.appga.junit5watcher

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(BenchmarkTestExtension::class)
@ExtendWith(BenchmarkExtension::class)
internal class DisabledMethodsInNestedClassTest {

    @Nested
    inner class NestedTests {
        @Test
        @Disabled
        fun methodTest() {
            // do nothing
        }
    }

    companion object {
        @TestFinalization
        @JvmStatic
        fun validateResults(metrics: Metrics, testClassCounters: TestClassCounters) {
            val className = DisabledMethodsInNestedClassTest::class.qualifiedName!!
            val nestedClassName = "$className$${NestedTests::class.simpleName}"
            val resultsMetrics = metrics.getResults().toMap()

            assertThat(testClassCounters.testCounter.get()).isEqualTo(0)

            assertThat(resultsMetrics)
                .`as`("Both parent and nested class are included in metrics")
                .containsOnlyKeys(className, nestedClassName)

            assertThat(resultsMetrics[className])
                .`as`("Only cumulative metric for parent class is included in metrics")
                .containsOnlyKeys(MetricType.CUMULATIVE)

            assertThat(resultsMetrics[className])
                .`as`("Only cumulative metric for nested class is included in metrics")
                .containsOnlyKeys(MetricType.CUMULATIVE)
        }
    }

}