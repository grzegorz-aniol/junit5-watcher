package pl.appga.junit5watcher

import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration

enum class MetricType { CUMULATIVE, BEFORE_ALL, BEFORE_EACH, TEST_ONLY, AFTER_EACH, AFTER_ALL }

private typealias TestResult = ConcurrentHashMap<MetricType, Duration>

internal class Metrics {
    private val entries: ConcurrentHashMap<String, TestResult> = ConcurrentHashMap()

    fun addResult(className: String, metricType: MetricType, duration: Duration) {
        val testResult = entries.getOrPut(className) { TestResult() }
        testResult.merge(metricType, duration) { d1, d2 -> d1 + d2 }
    }

    fun getResults(): Iterable<Pair<String, Map<MetricType, Duration>>> {
        return entries.asIterable().map { it.key to it.value }
    }
}