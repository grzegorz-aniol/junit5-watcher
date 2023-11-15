package pl.appga.junit5watcher

import org.assertj.core.api.Assertions
import org.assertj.core.data.Percentage
import kotlin.time.Duration

fun assertThat(classMetrics: Map<MetricType, Duration>,
               beforeAll: Double? = null,
               beforeEach: Double? = null,
               tests: Double? = null,
               afterEach: Double? = null,
               afterAll: Double? = null,
               cumulative: Double? = null,
               tolerance: Double = 0.2) {

    val comparePrecision = Percentage.withPercentage(100.0 * tolerance)

    val expectedCumulativeTime = cumulative
        ?: (classMetrics.entries.filterNot { it.key == MetricType.CUMULATIVE }
            .sumOf { it.value.toMills() ?: 0.0 })
    Assertions.assertThat(classMetrics[MetricType.CUMULATIVE].toMills() ?: 0.0)
        .`as`("Cumulative result")
        .isCloseTo(expectedCumulativeTime, comparePrecision)

    if (beforeAll != null) {
        Assertions.assertThat(classMetrics[MetricType.BEFORE_ALL].toMills() ?: 0.0)
            .`as`("BeforeAll result")
            .isCloseTo(beforeAll, comparePrecision)
    }

    if (beforeEach != null) {
        Assertions.assertThat(classMetrics[MetricType.BEFORE_EACH].toMills() ?: 0.0)
            .`as`("BeforeEach result")
            .isCloseTo(beforeEach, comparePrecision)
    }

    if (tests != null) {
        Assertions.assertThat(classMetrics[MetricType.TEST_ONLY].toMills() ?: 0.0)
            .`as`("Test result")
            .isCloseTo(tests, comparePrecision)
    }

    if (afterEach != null) {
        Assertions.assertThat(classMetrics[MetricType.AFTER_EACH].toMills() ?: 0.0)
            .`as`("AfterEach result")
            .isCloseTo(afterEach, comparePrecision)
    }

    if (afterAll != null) {
        Assertions.assertThat(classMetrics[MetricType.AFTER_ALL].toMills() ?: 0.0)
            .`as`("AfterAll result")
            .isCloseTo(afterAll, comparePrecision)
    }
}
