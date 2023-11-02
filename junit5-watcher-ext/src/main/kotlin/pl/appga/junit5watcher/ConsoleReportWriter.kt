package pl.appga.junit5watcher

import java.util.concurrent.atomic.AtomicLong
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.DurationUnit

internal class ConsoleReportWriter : ReportWriter {

    private val columnsOrdered = MetricType.entries
    private val log = LoggerFactory.getLogger(ConsoleReportWriter::class.java)

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
        val metricColumns = columnsOrdered.joinToString(",") { getMetricTypeLabel(it) }
        log.info("className,$metricColumns\n")
        val totalSum = MetricType.values().associateWith { AtomicLong(0) }
        results.sortedBy { entry -> entry.first }
            .forEach { (classFqn, metrics) ->
                val metricValues = columnsOrdered.joinToString(",") { metricType ->
                    val valueInMsc = metrics[metricType]?.toLong(DurationUnit.MILLISECONDS)
                    totalSum[metricType]?.addAndGet(valueInMsc ?: 0)
                    metrics[metricType]?.toLong(DurationUnit.MILLISECONDS)?.toString() ?: ""
                }
                log.info("$classFqn,$metricValues")
            }
        val totalSumValues = columnsOrdered.joinToString ("," ) { metricType ->
            totalSum[metricType]?.get().toString()
        }
        log.info("TOTAL,$totalSumValues")
    }

}