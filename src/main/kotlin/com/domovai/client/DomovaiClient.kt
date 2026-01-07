package com.domovai.client

import com.domovai.config.DomovaiConfig
import com.domovai.model.CodeReviewRequest
import com.domovai.model.CodeReviewResponse
import com.domovai.model.HealthResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class DomovaiClient(private val config: DomovaiConfig) {
    private val logger = LoggerFactory.getLogger(DomovaiClient::class.java)
    private val webClient: WebClient

    init {
        webClient = WebClient.builder()
            .baseUrl(config.apiUrl)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    fun checkHealth(): Mono<HealthResponse> {
        logger.info("Checking Domovai service health at {}", config.apiUrl)

        return webClient.get()
            .uri("/health")
            .retrieve()
            .onStatus({ status -> status.isError }) { response ->
                response.bodyToMono<String>().flatMap { body ->
                    Mono.error(
                        DomovaiApiException(
                            "API request failed: ${response.statusCode()} - $body",
                            response.statusCode().value()
                        )
                    )
                }
            }
            .bodyToMono(HealthResponse::class.java)
            .timeout(Duration.ofMillis(config.timeout))
            .onErrorMap { throwable ->
                when (throwable) {
                    is DomovaiClientException -> throwable
                    else -> DomovaiConnectionException("Failed to connect to Domovai service", throwable)
                }
            }
            .doOnSuccess { response ->
                logger.info("Health check successful: status={}, model={}", response.status, response.model)
            }
            .doOnError { throwable ->
                logger.error("Health check failed: {}", throwable.message, throwable)
            }
    }

    fun reviewCode(request: CodeReviewRequest): Mono<CodeReviewResponse> {
        logger.info("Sending code review request for {} code", request.language)

        return webClient.post()
            .uri("/review")
            .bodyValue(request)
            .retrieve()
            .onStatus({ status -> status.isError }) { response ->
                response.bodyToMono<String>().flatMap { body ->
                    Mono.error(
                        DomovaiApiException(
                            "Code review failed: ${response.statusCode()} - $body",
                            response.statusCode().value()
                        )
                    )
                }
            }
            .bodyToMono(CodeReviewResponse::class.java)
            .timeout(Duration.ofMillis(config.timeout))
            .onErrorMap { throwable ->
                when (throwable) {
                    is DomovaiClientException -> throwable
                    else -> DomovaiConnectionException("Failed to connect to Domovai service", throwable)
                }
            }
            .doOnSuccess { response ->
                logger.info("Code review completed. Bugs found: {}", response.bugsFound)
            }
            .doOnError { throwable ->
                logger.error("Code review failed: {}", throwable.message, throwable)
            }
    }
}
