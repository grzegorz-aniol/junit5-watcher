package pl.appga.junit5watcher

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(BenchmarkTestExtension::class)
@ExtendWith(BenchmarkExtension::class)
internal class DisabledMethodOnlyTest {

    @Disabled
    @Test
    fun disabledMethodTest() {
        // do nothing
    }

}