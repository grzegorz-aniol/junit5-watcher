package org.appga.junit5watcher.example.services

import mu.two.KotlinLogging
import org.springframework.stereotype.Service

@Service
class BarService {

    private val log = KotlinLogging.logger { }

    fun execute(): String {
        log.info { "Running BarService.execute" }
        return "default"
    }
}