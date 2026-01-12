package com.domovai.core.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient

@Configuration
@EnableConfigurationProperties(DomovaiProperties::class)
class RestClientConfig {

    @Bean
    fun restClientBuilder(properties: DomovaiProperties): RestClient.Builder {
        val requestFactory = JdkClientHttpRequestFactory().apply {
            //setConnectTimeout(properties.timeout)
            setReadTimeout(properties.timeout)
        }

        return RestClient.builder()
            .baseUrl(properties.apiUrl)
            .requestFactory(requestFactory)
    }
}
