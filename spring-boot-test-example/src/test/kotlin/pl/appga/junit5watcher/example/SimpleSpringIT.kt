package pl.appga.junit5watcher.example

import mu.two.KotlinLogging
import pl.appga.junit5watcher.BenchmarkExtension
import pl.appga.junit5watcher.example.extensions.OtherBenchmarkExtension
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners

@TestExecutionListeners(listeners = [FirstSpringTestExecutionListener::class, SecondSpringTestExecutionListener::class])
@ExtendWith(BenchmarkExtension::class)
@ExtendWith(OtherBenchmarkExtension::class)
@SpringBootTest
internal class SimpleSpringIT {

    private companion object {
        private val log = KotlinLogging.logger {}

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            log.info { "Before All ${SimpleSpringIT::class.simpleName}, sleep: 200" }
            Thread.sleep(200)
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            log.info { "After All ${SimpleSpringIT::class.simpleName}, sleep: 200" }
            Thread.sleep(200)
        }
    }

    @BeforeEach
    fun beforeEach() {
        log.info { "Before each ${SimpleSpringIT::class.simpleName}, sleep: 200" }
        Thread.sleep(200)
    }

    @AfterEach
    fun afterEach() {
        log.info { "After each ${SimpleSpringIT::class.simpleName}, sleep: 200" }
        Thread.sleep(200)
    }

    @Test
    fun `test method 1`() {
        log.info { "Test 1" }
        Thread.sleep(1000)
    }

}