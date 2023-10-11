package pl.appga.junit5watcher.example

import mu.two.KotlinLogging
import pl.appga.junit5watcher.BenchmarkExtension
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest

@ExtendWith(BenchmarkExtension::class)
@SpringBootTest
internal class OneMoreIT {

    private companion object {
        private val log = KotlinLogging.logger {}

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            log.info { "Before All ${OneMoreIT::class.simpleName}, sleep: 200" }
            Thread.sleep(200)
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            log.info { "After All ${OneMoreIT::class.simpleName}, sleep: 200" }
            Thread.sleep(200)
        }
    }

    @BeforeEach
    fun beforeEach() {
        log.info { "Before each ${OneMoreIT::class.simpleName}, sleep: 1200" }
        Thread.sleep(1200)
    }

    @AfterEach
    fun afterEach() {
        log.info { "After each ${OneMoreIT::class.simpleName}, sleep: 200" }
        Thread.sleep(200)
    }


    @Test
    fun `test method 1`() {
        log.info { "Test 1, sleep: 1000" }
        Thread.sleep(1000)
    }

    @Test
    fun `test method 2`() {
        log.info { "Test 2, sleep: 2000" }
        Thread.sleep(2000)
    }

    @Test
    fun `test method 3`() {
        log.info { "Test 3, sleep: 3000" }
        Thread.sleep(3000)
    }

}