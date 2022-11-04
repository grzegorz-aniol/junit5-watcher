package org.appga.junit5watcher.example.mockbeans.problem

import mu.KotlinLogging
import org.appga.junit5watcher.example.services.BarService
import org.appga.junit5watcher.example.services.FooService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

// These 3 test classes creates two Spring contexts - the last test class defines different set of mock beans

@SpringBootTest
class ContextWithMock1Test {

    private val log = KotlinLogging.logger { }

    @MockBean
    private lateinit var fooService: FooService

    @Test
    fun `method 1`() {
        log.info { "Test method" }
        Mockito.`when`(fooService.execute()).thenReturn("mock1")
        assertThat(fooService.execute()).isEqualTo("mock1")
        Mockito.verify(fooService).execute()
    }

}

@SpringBootTest
class ContextWithMock2Test {

    private val log = KotlinLogging.logger { }

    @MockBean
    private lateinit var fooService: FooService

    @Test
    fun `method 2`() {
        log.info { "Test method" }
        Mockito.`when`(fooService.execute()).thenReturn("mock2")
        assertThat(fooService.execute()).isEqualTo("mock2")
        Mockito.verify(fooService).execute()
    }

}

@SpringBootTest
class ContextWithMock3Test {

    private val log = KotlinLogging.logger { }

    @MockBean
    private lateinit var fooService: FooService

    // another bean used - new context need to be created
    @MockBean
    private lateinit var barService: BarService

    @Test
    fun `method 3`() {
        log.info { "Test method" }
        Mockito.`when`(fooService.execute()).thenReturn("mock3a")
        Mockito.`when`(barService.execute()).thenReturn("mock3b")
        assertThat(fooService.execute()).isEqualTo("mock3a")
        assertThat(barService.execute()).isEqualTo("mock3b")
        Mockito.verify(fooService).execute()
        Mockito.verify(barService).execute()
    }
}