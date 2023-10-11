package pl.appga.javaexample;

import pl.appga.junit5watcher.BenchmarkExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BenchmarkExtension.class)
public class Example2Test {

    @Test
    void test1() throws InterruptedException {
        Thread.sleep(1_000);
    }

    @Test
    void test2() throws InterruptedException {
        Thread.sleep(1_000);
    }

}
