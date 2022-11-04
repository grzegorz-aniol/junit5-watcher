package org.appga.junit5watcher.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Junit5WatcherApplication

fun main(args: Array<String>) {
    runApplication<Junit5WatcherApplication>(*args)
}
