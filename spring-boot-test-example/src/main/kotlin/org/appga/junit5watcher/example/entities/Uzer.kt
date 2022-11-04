package org.appga.junit5watcher.example.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Uzer (
    @Id
    var id: Long? = null,

    @Column
    var name: String,

    @Column
    var year: Int
)
