package org.appga.junit5watcher.example.services

import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class FooService {

    private val log = KotlinLogging.logger { }

    fun execute(): String {
        log.info { "Running FooService.execute" }
        return "default"
    }
}