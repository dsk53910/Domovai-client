package com.domovai.client

import com.domovai.model.CodeReviewRequest
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class DomovaiClientExample {

    @Bean
    fun exampleRunner(client: DomovaiClient): CommandLineRunner {
        return CommandLineRunner {
            println("=== Domovai Kotlin Client Example ===")

            // Check service health
            println("\n1. Checking service health...")
            client.checkHealth()
                .doOnSuccess { health ->
                    println("Service Status: ${health.status}")
                    println("Model: ${health.model}")
                }
                .doOnError { error ->
                    println("Health check failed: ${error.message}")
                }
                .block()

            // Review some code
            println("\n2. Reviewing Python code...")
            val pythonCode = """
                def calculate_factorial(n):
                    if n == 0:
                        return 1
                    else:
                        return n * calculate_factorial(n - 1)
            """.trimIndent()

            val request = CodeReviewRequest(pythonCode, "python")
            client.reviewCode(request)
                .doOnSuccess { response ->
                    println("Review Result:")
                    println(response.review)
                    println("Bugs Found: ${response.bugsFound}")
                }
                .doOnError { error ->
                    println("Code review failed: ${error.message}")
                }
                .block()

            println("\nExample completed!")
        }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(DomovaiClientExample::class.java, *args)
}
