package com.cookiss.util

import io.ktor.http.content.*
import java.io.File
import java.util.UUID

fun PartData.FileItem.save(path: String): String{
    val fileBytes = streamProvider().readBytes()
    val fileExtesion = originalFileName?.takeLastWhile { it != '.' }
    val fileName = UUID.randomUUID().toString() + "." + fileExtesion
    val folder = File(path)
    folder.mkdirs()
    File("$path$fileName").writeBytes(fileBytes)

    return fileName
}