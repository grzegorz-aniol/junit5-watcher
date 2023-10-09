package org.appga.javaexample;

import org.appga.junit5watcher.BenchmarkExtension;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BenchmarkExtension.class)
public class RepeatedScenarioTest {

    @Test
    void doIt() throws InterruptedException {
        Thread.sleep(2);
    }

    @Test
    void anotherTest() throws InterruptedException {
        Thread.sleep(2);
    }

    @RepeatedTest(10)
    void repeatedTest() throws InterruptedException {
        Thread.sleep(2);
    }

    @Test
    void normalTest() throws InterruptedException {
        Thread.sleep(2);
    }

    @Test
    void finalTest() throws InterruptedException {
        Thread.sleep(2);
    }

}
