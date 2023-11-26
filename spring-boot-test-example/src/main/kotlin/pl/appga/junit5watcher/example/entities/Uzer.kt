package pl.appga.junit5watcher.example.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Uzer (
    @Id
    var id: Long? = null,

    @Column
    var name: String,

    @Column
    var failedLoginCount: Int
)
