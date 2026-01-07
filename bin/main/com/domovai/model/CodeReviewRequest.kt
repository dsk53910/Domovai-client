package com.domovai.model

data class CodeReviewRequest(
    val code: String,
    val language: String = "python"
)
