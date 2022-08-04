package com.cookiss

import com.cookiss.di.mainModule
import com.cookiss.plugins.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import java.nio.file.Paths

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin){
        modules(mainModule)
    }
    configureRouting()
//    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()

    println(Paths.get("").toAbsolutePath().toString())

}
