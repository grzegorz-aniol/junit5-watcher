package pl.appga.junit5watcher

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import kotlin.time.DurationUnit

@ExtendWith(BenchmarkTestExtension::class)
@ExtendWith(BenchmarkExtension::class)
internal class ClassWithNestedTestsTest {

    @Test
    fun test1() {
        log.info { "${this.javaClass.simpleName} - test1" }
        Thread.sleep(50)
    }

    @Nested
    inner class NestedTest1 {

        @Test
        fun test1() {
            log.info { "${this.javaClass.simpleName} - test1" }
            Thread.sleep(25)
        }

        @Test
        fun test2() {
            log.info { "${this.javaClass.simpleName} - test2" }
            Thread.sleep(25)
        }

    }

    @Test
    fun test2() {
        log.info { "${this.javaClass.simpleName} - test2" }
        Thread.sleep(50)
    }

    @Nested
    inner class NestedTest2 {

        @Test
        fun test3() {
            log.info { "${this.javaClass.simpleName} - test3" }
            Thread.sleep(25)
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(ClassWithNestedTestsTest::class.java)

        @JvmStatic
        @BeforeAll
        fun setUp() {
            log.info { "ClassWithNestedTestsTest - before all" }
            Thread.sleep(100)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            log.info { "ClassWithNestedTestsTest - after all" }
            Thread.sleep(100)
        }
        
        @TestFinalization
        @JvmStatic
        @Suppress("unused")
        fun validateResults(metrics: Metrics, testClassCounters: TestClassCounters) {
            Assertions.assertThat(metrics.getResults()).hasSize(3)
            val enclosingClassName = ClassWithNestedTestsTest::class.qualifiedName!!
            val nestedClassName1 = "$enclosingClassName\$NestedTest1"
            val nestedClassName2 = "$enclosingClassName\$NestedTest2"
            val metricsMap = metrics.getResults().toMap()

            val metricEnclosing= metricsMap[enclosingClassName]!!
            val metricNested1= metricsMap[nestedClassName1]!!
            val metricNested2= metricsMap[nestedClassName2]!!

            assertThat(metricNested1, tests = 50.0)
            assertThat(metricNested2, tests = 25.0)

            assertThat(metricEnclosing, beforeAll = 100.0, tests = 100.0, afterAll = 100.0,
                cumulative = metricNested1[MetricType.CUMULATIVE]!!.toDouble(DurationUnit.MILLISECONDS)
                        + metricNested2[MetricType.CUMULATIVE]!!.toDouble(DurationUnit.MILLISECONDS)
                        + 300.0)
        }
    }
}