package pl.appga.javaexample;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.appga.junit5watcher.BenchmarkExtension;

@ExtendWith(BenchmarkExtension.class)
public class ExampleWithOneDisabledNestedTest {

    @Nested
    class NestedTest {

        @Test
        void test1() throws  InterruptedException{
            Thread.sleep(10);
        }

        @Disabled
        @Test
        void test2() throws InterruptedException {
            Thread.sleep(10);
        }

        @Test
        void test3() throws InterruptedException {
            Thread.sleep(10);
        }

    }

}
