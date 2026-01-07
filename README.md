# Domovai Kotlin Client

Kotlin client for the Domovai AI code review service.

## Overview

This Kotlin client provides a Spring Boot-based interface to interact with the Domovai Python API service. It uses Spring WebClient for reactive HTTP communication and includes comprehensive error handling, logging, and configuration management.

## Features

- **Reactive HTTP Client**: Uses Spring WebClient for non-blocking API calls
- **Comprehensive Error Handling**: Custom exceptions for different error scenarios
- **Configuration Management**: Externalized configuration via `application.properties`
- **Logging**: Detailed logging for debugging and monitoring
- **Unit Tests**: Comprehensive test coverage with MockWebServer
- **Kotlin Coroutines**: Full support for reactive programming

## Project Structure

```
domovai-client/
├── build.gradle.kts          # Gradle Kotlin DSL build configuration
├── settings.gradle.kts       # Gradle settings
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/
│   │   │       └── domovai/
│   │   │           ├── client/   # Main client implementation
│   │   │           ├── model/    # Data models
│   │   │           ├── config/   # Configuration
│   │   │           └── DomovaiClientApplication.kt
│   │   └── resources/
│   │       └── application.properties
│   └── test/                 # Unit tests
└── README.md
```

## Requirements

- Java 17+
- Kotlin 1.9+
- Gradle 8+

## Installation

1. Clone the repository or copy the project
2. Build the project:

```bash
cd domovai-client
./gradlew build
```

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Domovai API Configuration
domovai.apiUrl=http://localhost:8000
domovai.timeout=5000

# Server Configuration
server.port=8080

# Logging Configuration
logging.level.com.domovai=INFO
logging.level.org.springframework.web=INFO
```

## Usage

### As a Spring Boot Application

```kotlin
@SpringBootApplication
class MyApplication(val client: DomovaiClient)

fun main() {
    SpringApplication.run(MyApplication::class.java)
}
```

### Programmatic Usage

```kotlin
val config = DomovaiConfig(
    apiUrl = "http://localhost:8000",
    timeout = 5000
)

val client = DomovaiClient(config)

// Check service health
client.checkHealth()
    .subscribe { health ->
        println("Service status: ${health.status}")
    }

// Review code
val request = CodeReviewRequest(
    code = "def hello(): pass",
    language = "python"
)

client.reviewCode(request)
    .subscribe { response ->
        println("Review: ${response.review}")
        println("Bugs found: ${response.bugsFound}")
    }
```

## API Endpoints

### Health Check

```kotlin
client.checkHealth(): Mono<HealthResponse>
```

Returns:
- `status`: Service status ("ok" or error message)
- `model`: AI model being used

### Code Review

```kotlin
client.reviewCode(request: CodeReviewRequest): Mono<CodeReviewResponse>
```

Request:
- `code`: Source code to review
- `language`: Programming language (default: "python")

Returns:
- `review`: Detailed code review analysis
- `bugsFound`: Boolean indicating if bugs were detected

## Error Handling

The client provides comprehensive error handling:

- `DomovaiClientException`: Base exception class
- `DomovaiApiException`: API-specific errors (includes HTTP status code)
- `DomovaiConnectionException`: Connection/timeout errors

## Running Tests

```bash
./gradlew test
```

## Example Application

Run the example application to see the client in action:

```bash
./gradlew bootRun --args='--spring.main.sources=com.domovai.client.DomovaiClientExampleKt'
```

## Integration with Python Service

1. Start the Python FastAPI service: `python -m domovai.server.api`
2. Configure the Kotlin client to point to the Python service URL
3. Use the Kotlin client to interact with the Python API

## Dependencies

- Spring Boot 3.3.0
- Spring WebFlux (reactive WebClient)
- Jackson Kotlin Module
- Reactor Core
- Kotlin Coroutines Reactor
- MockWebServer (for testing)

## Building

```bash
./gradlew clean build
```

## Running

```bash
./gradlew bootRun
```

## License

TBD (same as parent project)
