package pl.appga.javaexample;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.appga.junit5watcher.BenchmarkExtension;

@ExtendWith(BenchmarkExtension.class)
@Disabled
public class ExampleWithDisabledClassTest {

    @Test
    void test1() throws InterruptedException {
        Thread.sleep(10);
    }

    @Test
    void test2() throws InterruptedException {
        Thread.sleep(10);
    }

}
