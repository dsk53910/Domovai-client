package com.domovai

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DomovaiClientApplication

fun main(args: Array<String>) {
    runApplication<DomovaiClientApplication>(*args)
}
