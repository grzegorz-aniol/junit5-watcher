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

internal enum class MarkType { BEFORE_ALL, BEFORE_EACH, BEFORE_TEST, AFTER_TEST, AFTER_EACH, AFTER_ALL, LAST }

internal data class TestClassCounters(
    val timeMarks: HashMap<MarkType, TimeSource.Monotonic.ValueTimeMark> = HashMap(),
    val testCounter: AtomicLong = AtomicLong(0L)
)

class BenchmarkExtension :
    BeforeAllCallback,
    BeforeEachCallback,
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback,
    AfterEachCallback,
    AfterAllCallback {

    private companion object {
        private var initialized: Boolean = false
        private val metrics: Metrics = Metrics
    }

    private val log = LoggerFactory.getLogger(BenchmarkExtension::class.java)
    private val timeSource = TimeSource.Monotonic
    private val contextNamespace = ExtensionContext.Namespace.create(BenchmarkExtension::class.java)

    /** Warning: `beforeAll` won't be executed for inner nested test with PER_METHOD lifecycle */
    override fun beforeAll(extensionContext: ExtensionContext) {
        val beforeAllMark = timeSource.markNow()
        log.debug { "Test ${extensionContext.testClass.get().name} - before all" }
        if (!initialized) {
            log.info { "BenchmarkExtension registered" }
            registerShutdownHook()
            initialized = true
        }
        storeTestClassCounters(extensionContext) // store all counters in test class context
        setTimeMark(extensionContext, MarkType.BEFORE_ALL, beforeAllMark)
    }

    override fun beforeEach(extensionContext: ExtensionContext) {
        val beforeEachMark = timeSource.markNow()
        log.debug { "Test ${extensionContext.testClass.get().name} - before each" }
        val classContext = extensionContext.parent.get()
        setTimeMark(classContext, MarkType.BEFORE_EACH, beforeEachMark)
        val testCount = getAndIncTestCounter(classContext)
        if (testCount == 0L) {
            measureAndSaveMetric(classContext, MarkType.BEFORE_ALL, MarkType.BEFORE_EACH, MetricType.BEFORE_ALL)
        }
    }

    override fun beforeTestExecution(extensionContext: ExtensionContext) {
        val beforeTestMark = timeSource.markNow()
        log.debug { "Test ${extensionContext.testClass.get().name} - before test execution" }
        val classContext = extensionContext.parent.get()
        setTimeMark(classContext, MarkType.BEFORE_TEST, beforeTestMark)
        measureAndSaveMetric(classContext, MarkType.BEFORE_EACH, MarkType.BEFORE_TEST, MetricType.BEFORE_EACH)
    }

    override fun afterTestExecution(extensionContext: ExtensionContext) {
        val afterTestMark = timeSource.markNow()
        log.debug { "Test ${extensionContext.testClass.get().name} - after test execution" }
        val classContext = extensionContext.parent.get()
        setTimeMark(classContext, MarkType.AFTER_TEST, afterTestMark)
        measureAndSaveMetric(classContext, MarkType.BEFORE_TEST, MarkType.AFTER_TEST, MetricType.TEST_ONLY)
    }

    override fun afterEach(extensionContext: ExtensionContext) {
        val afterEachMark = timeSource.markNow()
        log.debug { "Test ${extensionContext.testClass.get().name} - after each" }
        val classContext = extensionContext.parent.get()
        setTimeMark(classContext, MarkType.AFTER_EACH, afterEachMark)
        setTimeMark(classContext, MarkType.LAST, afterEachMark)
        measureAndSaveMetric(classContext, MarkType.AFTER_TEST, MarkType.AFTER_EACH, MetricType.AFTER_EACH)
        if (extensionContext.testClass.get().isMemberClass) {
            // for inner class store AFTER_EACH time mark for enclosing class
            // in order to calculate correctly AfterAll metric in that class
            setTimeMark(classContext.parent.get(), MarkType.LAST, afterEachMark)
        }
    }

    /** Warning: `afterAll` won't be executed for inner nested test with PER_METHOD lifecycle */
    override fun afterAll(extensionContext: ExtensionContext) {
        val afterAllMark = timeSource.markNow()
        log.debug { "Test ${extensionContext.testClass.get().name} - after all" }
        setTimeMark(extensionContext, MarkType.AFTER_ALL, afterAllMark)
        if (getTestClassCounters(extensionContext).testCounter.get() > 0) {
            measureAndSaveMetric(extensionContext, MarkType.LAST, MarkType.AFTER_ALL, MetricType.AFTER_ALL)
        }
        measureAndSaveMetric(extensionContext, MarkType.BEFORE_ALL, MarkType.AFTER_ALL, MetricType.CUMULATIVE)
        if (extensionContext.testClass.get().isMemberClass) {
            // for inner class store AFTER_EACH time mark for enclosing class
            // in order to calculate correctly AfterAll metric in that class
            setTimeMark(extensionContext.parent.get(), MarkType.LAST, afterAllMark)
        }
    }

    private fun measureAndSaveMetric(extensionContext: ExtensionContext, prevMarkType: MarkType, currentMarkType: MarkType, metricType: MetricType) {
        val prevMark = getTimeMark(extensionContext, prevMarkType)
        val currentMark = getTimeMark(extensionContext, currentMarkType)
        val elapsed: Duration = currentMark - prevMark
        log.trace { "Saving metric $metricType  for context '${extensionContext.displayName}' with marks ($prevMarkType=${prevMark}..$currentMarkType=$currentMark) to value: $elapsed" }
        metrics.addResult(extensionContext.testClass.get().name, metricType, elapsed)
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
        log.trace { "Set time mark $markType in context ${context.displayName} to $timeMark" }
        val testClassCounters = getTestClassCounters(context)
        testClassCounters.timeMarks[markType] = timeMark
    }

    private fun getAndIncTestCounter(context: ExtensionContext): Long  {
        val testClassCounters = getTestClassCounters(context)
        return testClassCounters.testCounter.getAndIncrement().also {
            log.trace { "Incrementing tests counter on context: ${context.displayName} to $it" }
        }
    }

    private fun registerShutdownHook() {
        log.debug { "Registering shutdown hook" }
        Runtime.getRuntime().addShutdownHook(Thread {
            log.info { "Saving test metrics report" }
            CsvReportWriter(File("./test-metrics-report.csv")).write(metrics.getResults())
        })
    }

}