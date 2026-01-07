package com.domovai.client

class DomovaiClientException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class DomovaiApiException(message: String, val statusCode: Int, cause: Throwable? = null) : DomovaiClientException(message, cause)

class DomovaiConnectionException(message: String, cause: Throwable? = null) : DomovaiClientException(message, cause)
