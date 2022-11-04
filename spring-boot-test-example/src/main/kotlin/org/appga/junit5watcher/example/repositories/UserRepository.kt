package org.appga.junit5watcher.example.repositories

import org.appga.junit5watcher.example.entities.Uzer
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<Uzer, Long>
