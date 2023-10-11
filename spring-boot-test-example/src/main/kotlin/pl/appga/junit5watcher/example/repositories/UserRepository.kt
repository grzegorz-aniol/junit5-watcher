package pl.appga.junit5watcher.example.repositories

import pl.appga.junit5watcher.example.entities.Uzer
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<Uzer, Long>
