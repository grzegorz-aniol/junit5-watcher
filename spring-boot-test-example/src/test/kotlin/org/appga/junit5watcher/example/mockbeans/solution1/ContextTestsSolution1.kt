package org.appga.junit5watcher.example.mockbeans.solution1

import mu.KotlinLogging
import org.appga.junit5watcher.example.services.BarService
import org.appga.junit5watcher.example.services.FooService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean

// These 3 test classes creates just one Spring contexts - all test classes share same configuration with same set of mocked classes

@TestConfiguration
private class Configuration {

    @MockBean
    private lateinit var fooService: FooService

    @MockBean
    private lateinit var barService: BarService

}

@SpringBootTest(classes = [Configuration::class])
class ContextWithMock1SolutionTest {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var fooService: FooService

    @Test
    fun `method 1`() {
        log.info { "Test method" }
        Mockito.`when`(fooService.execute()).thenReturn("mock1")
        Assertions.assertThat(fooService.execute()).isEqualTo("mock1")
        Mockito.verify(fooService).execute()
    }

}

@SpringBootTest(classes = [Configuration::class])
class ContextWithMock2SolutionTest {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var fooService: FooService

    @Test
    fun `method 2`() {
        log.info { "Test method" }
        Mockito.`when`(fooService.execute()).thenReturn("mock2")
        Assertions.assertThat(fooService.execute()).isEqualTo("mock2")
        Mockito.verify(fooService).execute()
    }

}

@SpringBootTest(classes = [Configuration::class])
class ContextWithMock3SolutionTest {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var fooService: FooService

    // another bean used - new context need to be created
    @Autowired
    private lateinit var barService: BarService

    @Test
    fun `method 3`() {
        log.info { "Test method" }
        Mockito.`when`(fooService.execute()).thenReturn("mock3a")
        Mockito.`when`(barService.execute()).thenReturn("mock3b")
        Assertions.assertThat(fooService.execute()).isEqualTo("mock3a")
        Assertions.assertThat(barService.execute()).isEqualTo("mock3b")
        Mockito.verify(fooService).execute()
        Mockito.verify(barService).execute()
    }

}