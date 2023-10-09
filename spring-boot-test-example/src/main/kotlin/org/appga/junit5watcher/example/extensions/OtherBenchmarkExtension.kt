package org.appga.junit5watcher.example.extensions

import mu.two.KotlinLogging
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

class OtherBenchmarkExtension : BeforeAllCallback, BeforeEachCallback, AfterAllCallback, AfterEachCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private val log = KotlinLogging.logger { }

    override fun beforeAll(p0: ExtensionContext?) {
        log.info { "${this::class.simpleName} beforeAll" }
    }

    override fun beforeEach(p0: ExtensionContext?) {
        log.info { "${this::class.simpleName} beforeEach" }
    }

    override fun afterAll(p0: ExtensionContext?) {
        log.info { "${this::class.simpleName} afterAll" }
    }

    override fun afterEach(p0: ExtensionContext?) {
        log.info { "${this::class.simpleName} afterEach" }
    }

    override fun beforeTestExecution(p0: ExtensionContext?) {
        log.info { "${this::class.simpleName} beforeTestExecution" }
    }

    override fun afterTestExecution(p0: ExtensionContext?) {
        log.info { "${this::class.simpleName} afterTestExecution" }
    }
}