package org.appga.junit5watcher

import org.assertj.core.api.Assertions
import org.assertj.core.data.Offset
import org.slf4j.LoggerFactory
import kotlin.time.Duration

fun assertThat(classMetrics: Map<MetricType, Duration>,
               beforeAll: Double? = null,
               beforeEach: Double? = null,
               tests: Double? = null,
               afterEach: Double? = null,
               afterAll: Double? = null) {

    val actualCumulative = classMetrics.entries.filterNot { it.key == MetricType.CUMULATIVE }.sumOf { it.value.toMills() ?: 0.0 }
    val offset = Offset.offset(20.0)

    Assertions.assertThat(classMetrics[MetricType.CUMULATIVE].toMills() ?: 0.0)
        .`as`("Cumulative result")
        .isCloseTo(actualCumulative, offset)

    if (beforeAll != null) {
        Assertions.assertThat(classMetrics[MetricType.BEFORE_ALL].toMills() ?: 0.0)
            .`as`("BeforeAll result")
            .isCloseTo(beforeAll, offset)
    }

    if (beforeEach != null) {
        Assertions.assertThat(classMetrics[MetricType.BEFORE_EACH].toMills() ?: 0.0)
            .`as`("BeforeEach result")
            .isCloseTo(beforeEach, offset)
    }

    if (tests != null) {
        Assertions.assertThat(classMetrics[MetricType.TEST_ONLY].toMills() ?: 0.0)
            .`as`("Test result")
            .isCloseTo(tests, offset)
    }

    if (afterEach != null) {
        Assertions.assertThat(classMetrics[MetricType.AFTER_EACH].toMills() ?: 0.0)
            .`as`("AfterEach result")
            .isCloseTo(afterEach, offset)
    }

    if (afterAll != null) {
        Assertions.assertThat(classMetrics[MetricType.AFTER_ALL].toMills() ?: 0.0)
            .`as`("AfterAll result")
            .isCloseTo(afterAll, offset)
    }
}
