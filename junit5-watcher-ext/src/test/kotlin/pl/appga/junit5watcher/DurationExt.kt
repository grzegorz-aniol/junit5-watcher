package pl.appga.junit5watcher

import kotlin.time.Duration
import kotlin.time.DurationUnit

internal fun Duration?.toMills() = this?.toDouble(DurationUnit.MILLISECONDS)