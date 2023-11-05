package pl.appga.junit5watcher

import java.io.File
import java.util.concurrent.atomic.AtomicLong
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.TimeSource

internal enum class MarkType { BEFORE_ALL, BEFORE_EACH, BEFORE_TEST, AFTER_TEST, AFTER_EACH, AFTER_ALL }

internal data class TestClassCounters(
    val timeMarks: HashMap<MarkType, TimeSource.Monotonic.ValueTimeMark> = HashMap(),
    val testCounter: AtomicLong = AtomicLong(0L)
)

class BenchmarkExtension : BeforeAllCallback, BeforeEachCallback, AfterAllCallback, AfterEachCallback,
    BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private companion object {
        private var initialized: Boolean = false
    }

    private val log = LoggerFactory.getLogger(BenchmarkExtension::class.java)
    private val timeSource = TimeSource.Monotonic
    private val contextNamespace = ExtensionContext.Namespace.create(BenchmarkExtension::class.java)

    override fun beforeAll(extensionContext: ExtensionContext) {
        log.debug { "Test ${extensionContext.testClass.get().name} - before all" }
        if (!initialized) {
            log.info { "BenchmarkExtension registered" }
            registerShutdownHook(extensionContext)
            initialized = true
        }
        storeTestClassCounters(extensionContext) // store all counters in test class context
        setTimeMark(extensionContext, MarkType.BEFORE_ALL, timeSource.markNow())
    }

    override fun beforeEach(extensionContext: ExtensionContext) {
        log.debug { "Test ${extensionContext.testClass.get().name} - before each" }

        val beforeEachMark = timeSource.markNow()
        val classContext = extensionContext.parent.get()
        setTimeMark(classContext, MarkType.BEFORE_EACH, beforeEachMark)
        val testCount = getAndIncTestCounter(classContext)
        if (testCount == 0L) {
            measureAndSaveMetric(classContext, MarkType.BEFORE_ALL, MarkType.BEFORE_EACH, MetricType.BEFORE_ALL)
        }
    }

    override fun beforeTestExecution(extensionContext: ExtensionContext) {
        log.debug { "Test ${extensionContext.testClass.get().name} - before test execution" }
        val beforeTestMark = timeSource.markNow()
        val classContext = extensionContext.parent.get()
        setTimeMark(classContext, MarkType.BEFORE_TEST, beforeTestMark)
        measureAndSaveMetric(classContext, MarkType.BEFORE_EACH, MarkType.BEFORE_TEST, MetricType.BEFORE_EACH)
    }

    override fun afterTestExecution(extensionContext: ExtensionContext) {
        log.debug { "Test ${extensionContext.testClass.get().name} - after test execution" }
        val afterTestMark = timeSource.markNow()
        val classContext = extensionContext.parent.get()
        setTimeMark(classContext, MarkType.AFTER_TEST, afterTestMark)
        measureAndSaveMetric(classContext, MarkType.BEFORE_TEST, MarkType.AFTER_TEST, MetricType.TEST_ONLY)
    }

    override fun afterEach(extensionContext: ExtensionContext) {
        log.debug { "Test ${extensionContext.testClass.get().name} - after each" }
        val afterEachMark = timeSource.markNow()
        val classContext = extensionContext.parent.get()
        setTimeMark(classContext, MarkType.AFTER_EACH, afterEachMark)
        measureAndSaveMetric(classContext, MarkType.AFTER_TEST, MarkType.AFTER_EACH, MetricType.AFTER_EACH)
    }

    override fun afterAll(extensionContext: ExtensionContext) {
        log.debug { "Test ${extensionContext.testClass.get().name} - after all" }
        val afterAllMark = timeSource.markNow()
        setTimeMark(extensionContext, MarkType.AFTER_ALL, afterAllMark)
        if (getTestClassCounters(extensionContext).testCounter.get() > 0) {
            measureAndSaveMetric(extensionContext, MarkType.AFTER_EACH, MarkType.AFTER_ALL, MetricType.AFTER_ALL)
        }
        measureAndSaveMetric(extensionContext, MarkType.BEFORE_ALL, MarkType.AFTER_ALL, MetricType.CUMULATIVE)
    }

    private fun measureAndSaveMetric(extensionContext: ExtensionContext, prevMarkType: MarkType, currentMarkType: MarkType, metricType: MetricType) {
        val prevMark = getTimeMark(extensionContext, prevMarkType)
        val currentMark = getTimeMark(extensionContext, currentMarkType)
        val elapsed: Duration = currentMark - prevMark
        getOrCreateMetrics(extensionContext).addResult(extensionContext.testClass.get().name, metricType, elapsed)
    }

    private fun getOrCreateMetrics(context: ExtensionContext): Metrics {
        // metric object is always stored in root context
        val store = context.root.getStore(contextNamespace)
        return store.getOrComputeIfAbsent(Metrics::class.java, { Metrics() }, Metrics::class.java)
    }

    private fun getTestClassCounters(context: ExtensionContext): TestClassCounters {
        val store = context.getStore(contextNamespace)
        return store.get(TestClassCounters::class.java) as? TestClassCounters
            ?: throw RuntimeException("Cannot find time marks")
    }

    private fun storeTestClassCounters(context: ExtensionContext) {
        val store = context.getStore(contextNamespace)
        store.put(TestClassCounters::class.java, TestClassCounters())
    }

    private fun getTimeMark(context: ExtensionContext, markType: MarkType): TimeSource.Monotonic.ValueTimeMark {
        val testClassCounters = getTestClassCounters(context)
        return testClassCounters.timeMarks[markType]
            ?: throw RuntimeException("Cannot find time mark for type $markType")
    }

    private fun setTimeMark(context: ExtensionContext, markType: MarkType, timeMark: TimeSource.Monotonic.ValueTimeMark) {
        val testClassCounters = getTestClassCounters(context)
        testClassCounters.timeMarks[markType] = timeMark
    }

    private fun getAndIncTestCounter(context: ExtensionContext): Long  {
        val testClassCounters = getTestClassCounters(context)
        return testClassCounters.testCounter.getAndIncrement()
    }

    private fun registerShutdownHook(extensionContext: ExtensionContext) {
        log.debug { "Registering shutdown hook" }
        val metrics = getOrCreateMetrics(extensionContext)
        Runtime.getRuntime().addShutdownHook(Thread {
            log.info { "Saving test metrics report" }
            CsvReportWriter(File("./test-metrics-report.csv")).write(metrics.getResults())
            ConsoleReportWriter().write(metrics.getResults())
        })
    }

}