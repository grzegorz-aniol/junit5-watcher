package pl.appga.javaexample;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.appga.junit5watcher.BenchmarkExtension;

@ExtendWith(BenchmarkExtension.class)
public class ExampleWithOnlyDisabledTest {

    @Disabled
    @Test
    void test1() throws InterruptedException {
        Thread.sleep(10);
    }

}
