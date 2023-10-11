package pl.appga.junit5watcher.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class Junit5WatcherApplication

fun main(args: Array<String>) {
    runApplication<Junit5WatcherApplication>(*args)
}
