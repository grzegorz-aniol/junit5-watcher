package org.appga.junit5watcher

import java.io.FileOutputStream
import java.io.OutputStream
import mu.KotlinLogging

fun OutputStream.writeCsv(resultsMap: Map<String, Long>) {
    val writer = bufferedWriter()
    writer.write("cls_name,time_ms\n")
    resultsMap.asSequence()
        .map { it.key to it.value }
        .sortedByDescending { it.second }
        .forEach { entry ->
            writer.write("${entry.first},${entry.second}\n")
        }
    writer.flush()
}

object StatisticsExport {

    private val log = KotlinLogging.logger { }

    fun exportToCsv(title: String, resultMap: Map<String, Long>, fileName: String) {
        log.info { "Exporting '$title' to file '$fileName'" }
        if (resultMap.isEmpty()) {
            return
        }
        FileOutputStream(fileName).use {
            it.writeCsv(resultMap)
        }
    }
}
