package org.appga.junit5watcher

import mu.KotlinLogging
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

class BenchmarkExtension() : BeforeAllCallback, BeforeEachCallback, AfterAllCallback, AfterEachCallback,
    BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private var beforeAllTs: Long? = null
    private var beforeEachTs: Long? = null
    private var afterEachTs: Long? = null
    private var afterAllTs: Long? = null
    private var testStartTs: Long? = null
    private var testEndTs: Long? = null
    private var testCount: Long = 0

    private val beforeAllTotal = TimeMeasure("Before All time")
    private val beforeEachTotal = TimeMeasure("Before Each time")
    private val afterEachTotal = TimeMeasure("After Each time")
    private val afterAllTotal = TimeMeasure("After All time")
    private val testTotal = TimeMeasure("Tests time")

    private companion object {
        private val log = KotlinLogging.logger { }

        private var totalStartTs: Long? = null
        private val cumBeforeAllTotal = TimeMeasure("Before All cumulative time")
        private val cumBeforeEachTotal = TimeMeasure("Before Each cumulative time")
        private val cumAfterEachTotal = TimeMeasure("After Each cumulative time")
        private val cumAfterAllTotal = TimeMeasure("After All cumulative time")
        private val cumTestTotal = TimeMeasure("All tests cumulative time")

        private var isShutdownHookInitialized: Boolean = false
        private val totalExecutionTimePerClass = mutableMapOf<String, Long>()
        private val beforeAllTimePerClass = mutableMapOf<String, Long>()
        private val beforeEachTimePerClass = mutableMapOf<String, Long>()
        private val testsOnlyExecutionTimePerClass = mutableMapOf<String, Long>()
    }

    init {
        if (!isShutdownHookInitialized) {
            isShutdownHookInitialized = true;
            Runtime.getRuntime().addShutdownHook(Thread {
                with(StatisticsExport) {
                    exportToCsv("Class total execution time desc", totalExecutionTimePerClass, "result_total_time.csv")
                    exportToCsv("Before All execution time per class", beforeAllTimePerClass, "result_before_all_time.csv")
                    exportToCsv("Before Each execution time per class", beforeEachTimePerClass, "result_before_each_time.csv")
                    exportToCsv("Tests execution time only", testsOnlyExecutionTimePerClass, "result_test_only_time.csv")
                }
            })
        }
    }

    override fun beforeAll(extensionContext: ExtensionContext) {
        log.info { "${this::class.simpleName} beforeAll, instance: $this" }
        beforeAllTotal.reset()
        beforeEachTotal.reset()
        afterEachTotal.reset()
        afterEachTotal.reset()
        testTotal.reset()
        beforeAllTs = System.currentTimeMillis()
        if (totalStartTs == null) {
            totalStartTs = beforeAllTs
        }
        beforeEachTs = null
        afterEachTs = null
        afterAllTs = null
        testStartTs = null
        testEndTs = null
        testCount = 0L
    }

    override fun beforeEach(extensionContext: ExtensionContext) {
        log.info { "${this::class.simpleName} beforeEach, instance: $this" }
        beforeEachTs = System.currentTimeMillis()
        if (testCount == 0L) {
            measurePeriod(beforeAllTs, beforeEachTs, beforeAllTotal, "beforeAll")
        }
        ++testCount
    }

    override fun beforeTestExecution(extensionContext: ExtensionContext) {
        log.info { "${this::class.simpleName} beforeTestExecution, instance: $this" }
        testStartTs = System.currentTimeMillis()
        measurePeriod(beforeEachTs, testStartTs, beforeEachTotal, "beforeEach")
    }

    override fun afterTestExecution(extensionContext: ExtensionContext) {
        log.info { "${this::class.simpleName} afterTestExecution, instance: $this" }
        testEndTs = System.currentTimeMillis()
        measurePeriod(testStartTs, testEndTs, testTotal, "test")
    }

    override fun afterEach(extensionContext: ExtensionContext) {
        log.info { "${this::class.simpleName} afterEach, instance: $this" }
        afterEachTs = System.currentTimeMillis()
        measurePeriod(testEndTs, afterEachTs, afterEachTotal, "afterEach")
    }

    override fun afterAll(extensionContext: ExtensionContext) {
        log.info { "${this::class.simpleName} afterAll, instance: $this" }
        afterAllTs = System.currentTimeMillis()
        measurePeriod(afterEachTs ?: beforeAllTs, afterAllTs, afterAllTotal, "afterAll")

        // update 'per class' statistics
        val clsName = extensionContext.testClass.get().canonicalName
        safeLet(afterAllTs, beforeAllTs) { t1, t0 ->
            totalExecutionTimePerClass[clsName] = t1 - t0
        }
        beforeAllTimePerClass[clsName] = beforeAllTotal.time
        beforeEachTimePerClass[clsName] = beforeEachTotal.time
        testTotal.time.let {
            testsOnlyExecutionTimePerClass[clsName] = it
        }

        aggregateTotalSum()
        printTimeReport(extensionContext.testClass.get().canonicalName)
    }

    private fun aggregateTotalSum() {
        cumBeforeAllTotal.add(beforeAllTotal)
        cumBeforeEachTotal.add(beforeEachTotal)
        cumTestTotal.add(testTotal)
        cumAfterEachTotal.add(afterEachTotal)
        cumAfterAllTotal.add(afterAllTotal)
    }

    private fun printTimeReport(canonicalName: String) {
        beforeAllTotal.logResult(canonicalName)
        beforeEachTotal.logResult(canonicalName)
        testTotal.logResult(canonicalName)
        afterEachTotal.logResult(canonicalName)
        afterAllTotal.logResult(canonicalName)

        val totalTime = totalStartTs?.let { System.currentTimeMillis() - it }
        cumBeforeAllTotal.logResult(totalTime = totalTime)
        cumBeforeEachTotal.logResult(totalTime = totalTime)
        cumTestTotal.logResult(totalTime = totalTime)
        cumAfterEachTotal.logResult(totalTime = totalTime)
        cumAfterAllTotal.logResult(totalTime = totalTime)
    }

    private fun measurePeriod(t0: Long?, t1: Long?, timeMeasure: TimeMeasure, description: String) {
        if (t0 != null && t1 != null && t1 >= t0) {
            val delta = timeMeasure.add(t1 - t0)
            log.debug { "Time measured for '$description': $delta" }
        } else {
            log.warn { "Invalid time measure for '$description'" }
        }
    }

    private inline fun <T1: Any, T2: Any, R: Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2)->R?): R? {
        return if (p1 != null && p2 != null) block(p1, p2) else null
    }
}