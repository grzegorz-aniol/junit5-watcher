package pl.appga.junit5watcher

import kotlin.time.Duration

interface ReportWriter {
    fun write(results: Iterable<Pair<String, Map<MetricType, Duration>>>)
}
