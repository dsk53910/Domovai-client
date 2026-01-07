package com.domovai.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "domovai")
data class DomovaiConfig(
    var apiUrl: String = "http://localhost:8000",
    var timeout: Long = 5000
)
