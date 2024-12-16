package com.arnas.klatrebackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@SpringBootApplication
class KlatreBackendApplication

fun main(args: Array<String>) {
    runApplication<KlatreBackendApplication>(*args)
}

@Configuration
class ApplicationNoSecurity {
    @Bean fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity -> web.ignoring()
            .requestMatchers(AntPathRequestMatcher("/**"))}
    }
}