package com.arnas.klatrebackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity


@SpringBootApplication
@EnableAspectJAutoProxy
@EnableWebSecurity
class KlatreBackendApplication

fun main(args: Array<String>) {
    runApplication<KlatreBackendApplication>(*args)
}
