package com.domovai.core.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "domovai")
data class DomovaiProperties(
    val apiUrl: String = "http://localhost:8000",
    val timeout: Duration = Duration.ofSeconds(5)
)
