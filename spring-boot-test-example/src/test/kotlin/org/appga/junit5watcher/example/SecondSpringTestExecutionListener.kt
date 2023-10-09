package org.appga.junit5watcher.example

import mu.two.KotlinLogging
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class SecondSpringTestExecutionListener : TestExecutionListener {

    private val log = KotlinLogging.logger {}

    override fun beforeTestClass(testContext: TestContext) {
        log.info { "beforeTestClass" }
    }

    override fun prepareTestInstance(testContext: TestContext) {
        log.info { "prepareTestInstance" }
    }

    override fun beforeTestMethod(testContext: TestContext) {
        log.info { "beforeTestMethod" }
    }

    override fun beforeTestExecution(testContext: TestContext) {
        log.info { "beforeTestExecution" }
    }

    override fun afterTestExecution(testContext: TestContext) {
        log.info { "afterTestExecution" }
    }

    override fun afterTestMethod(testContext: TestContext) {
        log.info { "afterTestMethod" }
    }

    override fun afterTestClass(testContext: TestContext) {
        log.info { "afterTestClass" }
    }
}