package pl.appga.javaexample;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.appga.junit5watcher.BenchmarkExtension;

@ExtendWith(BenchmarkExtension.class)
public class ExampleWithMultipleNestedClassesTest {

    @BeforeAll
    static void setUp() throws InterruptedException {
        Thread.sleep(100);
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        Thread.sleep(100);
    }

    @Test
    void test1() throws InterruptedException {
        Thread.sleep(10);
    }

    @Test
    void test2() throws InterruptedException {
        Thread.sleep(10);
    }

    @Nested
    class Nested1 {
        @Test
        void nested1test1() throws InterruptedException {
            Thread.sleep(10);
        }
        @Test
        void nested1test2() throws InterruptedException {
            Thread.sleep(10);
        }
        @Test
        void nested1test3() throws InterruptedException {
            Thread.sleep(10);
        }
    }

    @Nested
    class Nested2 {
        @Test
        void nested2test1() throws InterruptedException {
            Thread.sleep(10);
        }
        @Test
        void nested2test2() throws InterruptedException {
            Thread.sleep(10);
        }
        @Test
        void nested2test3() throws InterruptedException {
            Thread.sleep(10);
        }
    }

}
