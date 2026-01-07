package com.domovai.client

import com.domovai.config.DomovaiConfig
import com.domovai.model.CodeReviewRequest
import com.domovai.model.CodeReviewResponse
import com.domovai.model.HealthResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.Duration

class DomovaiClientTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var domovaiClient: DomovaiClient

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val config = DomovaiConfig(
            apiUrl = mockWebServer.url("/").toString(),
            timeout = 5000
        )

        domovaiClient = DomovaiClient(config)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test health check success`() {
        val mockResponse = MockResponse()
            .setBody("{\"status\": \"ok\", \"model\": \"qwen2.5-coder:7b\"}")
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")

        mockWebServer.enqueue(mockResponse)

        val result = domovaiClient.checkHealth()

        StepVerifier.create(result)
            .expectNext(HealthResponse("ok", "qwen2.5-coder:7b"))
            .verifyComplete()
    }

    @Test
    fun `test health check failure`() {
        val mockResponse = MockResponse()
            .setBody("{\"detail\": \"Service unavailable\"}")
            .setResponseCode(503)
            .addHeader("Content-Type", "application/json")

        mockWebServer.enqueue(mockResponse)

        val result = domovaiClient.checkHealth()

        StepVerifier.create(result)
            .expectError(DomovaiApiException::class.java)
            .verify()
    }

    @Test
    fun `test code review success`() {
        val mockResponse = MockResponse()
            .setBody("{\"review\": \"Code looks good\", \"bugs_found\": false}")
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")

        mockWebServer.enqueue(mockResponse)

        val request = CodeReviewRequest("def hello(): pass", "python")
        val result = domovaiClient.reviewCode(request)

        StepVerifier.create(result)
            .expectNext(CodeReviewResponse("Code looks good", false))
            .verifyComplete()
    }

    @Test
    fun `test code review with bugs`() {
        val mockResponse = MockResponse()
            .setBody("{\"review\": \"Found potential bug in line 5\", \"bugs_found\": true}")
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")

        mockWebServer.enqueue(mockResponse)

        val request = CodeReviewRequest("def buggy_code(): return 1/0", "python")
        val result = domovaiClient.reviewCode(request)

        StepVerifier.create(result)
            .expectNext(CodeReviewResponse("Found potential bug in line 5", true))
            .verifyComplete()
    }

    @Test
    fun `test connection timeout`() {
        // Don't enqueue any response to simulate timeout
        val request = CodeReviewRequest("def test(): pass", "python")
        val result = domovaiClient.reviewCode(request)

        StepVerifier.create(result)
            .expectError(DomovaiConnectionException::class.java)
            .verify(Duration.ofSeconds(2))
    }
}
