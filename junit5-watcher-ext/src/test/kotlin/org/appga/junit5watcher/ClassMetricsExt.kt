package org.appga.junit5watcher

import org.assertj.core.api.Assertions
import org.assertj.core.data.Offset
import kotlin.time.Duration

fun assertThat(classMetrics: Map<MetricType, Duration>, beforeAll: Double = 0.0, beforeEach: Double = 0.0,
               tests: Double = 0.0,
               afterEach: Double = 0.0, afterAll: Double = 0.0,
               cumulative: Double = beforeAll + beforeEach + tests + afterEach + afterAll,
               skipBeforeEach: Boolean = false) {
    val precision = Offset.offset(20.0)
    Assertions.assertThat(classMetrics[MetricType.BEFORE_ALL].toMills() ?: 0.0).isCloseTo(beforeAll, precision)
    if (!skipBeforeEach) {
        Assertions.assertThat(classMetrics[MetricType.BEFORE_EACH].toMills() ?: 0.0).isCloseTo(beforeEach, precision)
    }
    Assertions.assertThat(classMetrics[MetricType.TEST_ONLY].toMills() ?: 0.0).isCloseTo(tests, precision)
    Assertions.assertThat(classMetrics[MetricType.AFTER_EACH].toMills() ?: 0.0).isCloseTo(afterEach, precision)
    Assertions.assertThat(classMetrics[MetricType.AFTER_ALL].toMills() ?: 0.0).isCloseTo(afterAll, precision)
}
