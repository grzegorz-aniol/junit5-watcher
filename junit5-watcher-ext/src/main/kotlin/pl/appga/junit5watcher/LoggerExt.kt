package pl.appga.junit5watcher

import org.slf4j.Logger

fun Logger.warn(messageProvider: ()->String) = warn(messageProvider())
fun Logger.info(messageProvider: ()->String) = info(messageProvider())
fun Logger.debug(messageProvider: ()->String) = debug(messageProvider())
fun Logger.trace(messageProvider: ()->String) = trace(messageProvider())
