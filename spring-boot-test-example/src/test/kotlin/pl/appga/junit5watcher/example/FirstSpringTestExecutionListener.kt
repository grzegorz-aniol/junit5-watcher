package pl.appga.junit5watcher.example

import mu.two.KotlinLogging
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class FirstSpringTestExecutionListener : TestExecutionListener {

    private val log = KotlinLogging.logger {}

    override fun beforeTestClass(testContext: TestContext) {
        log.info { "beforeTestClass, instance: $this, delay: 200" }
        Thread.sleep(200)
    }

    override fun prepareTestInstance(testContext: TestContext) {
        log.info { "prepareTestInstance, instance: $this, delay: 200" }
        Thread.sleep(200)
    }

    override fun beforeTestMethod(testContext: TestContext) {
        log.info { "beforeTestMethod, instance: $this, delay: 200" }
        Thread.sleep(200)
    }

    override fun beforeTestExecution(testContext: TestContext) {
        log.info { "beforeTestExecution, instance: $this," }
    }

    override fun afterTestExecution(testContext: TestContext) {
        log.info { "afterTestExecution, instance: $this" }
    }

    override fun afterTestMethod(testContext: TestContext) {
        log.info { "afterTestMethod, instance: $this, delay: 200" }
        Thread.sleep(200)
    }

    override fun afterTestClass(testContext: TestContext) {
        log.info { "afterTestClass, instance: $this, delay: 200" }
        Thread.sleep(200)
    }
}