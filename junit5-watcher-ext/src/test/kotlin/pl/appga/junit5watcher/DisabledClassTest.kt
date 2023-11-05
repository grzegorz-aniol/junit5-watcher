package pl.appga.junit5watcher

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(BenchmarkTestExtension::class)
@ExtendWith(BenchmarkExtension::class)
@Disabled
internal class DisabledClassTest {

    @Test
    fun methodTest() {
        // do nothing
    }

}