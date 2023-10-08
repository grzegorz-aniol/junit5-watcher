package org.appga.junit5watcher

import java.io.File
import java.io.FileOutputStream
import kotlin.time.Duration
import kotlin.time.DurationUnit

class CsvReportWriter(private val file: File) : ReportWriter {

    private val columnsOrdered = MetricType.entries

    private fun getMetricTypeLabel(metricType: MetricType): String =
        when (metricType) {
            MetricType.CUMULATIVE -> "CumulativeMs"
            MetricType.BEFORE_ALL -> "BeforeAllMs"
            MetricType.BEFORE_EACH -> "BeforeEachMs"
            MetricType.TEST_ONLY -> "TestOnlyMs"
            MetricType.AFTER_EACH -> "AfterEachMs"
            MetricType.AFTER_ALL -> "AfterAllMs"
        }

    override fun write(results: Iterable<Pair<String, Map<MetricType, Duration>>>) {
        FileOutputStream(file).use { output ->
            output.bufferedWriter().use { writer ->
                val metricColumns = columnsOrdered.joinToString(",") { getMetricTypeLabel(it) }
                writer.write("className,$metricColumns\n")
                results.sortedBy { entry -> entry.first }
                    .forEach { entry ->
                        val metricValues = columnsOrdered.joinToString(",") { metricType ->
                            entry.second[metricType]?.toLong(DurationUnit.MILLISECONDS)?.toString() ?: ""
                        }
                        writer.write("${entry.first},$metricValues\n")
                    }
            }
        }
    }

}