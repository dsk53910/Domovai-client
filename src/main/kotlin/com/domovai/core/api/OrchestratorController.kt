package com.domovai.core.api

import com.domovai.core.DomovaiClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// Простая DTO для фронтенда (Kotlin стиль)
data class UserRequest(val sourceCode: String)

@RestController
@RequestMapping("/api/orchestrator")
class OrchestratorController(
    // Внедряем наш Java-компонент!
    private val aiClient: DomovaiClient
) {

    @PostMapping("/check")
    fun checkCode(@RequestBody request: UserRequest): String {
        println("🤖 Оркестратор: Получен код от пользователя, вызываю Java-SDK...")

        // Вызов Java метода из Kotlin.
        // Kotlin видит Java Records как обычные классы с геттерами (или полями).
        val aiResponse = aiClient.scanCode(request.sourceCode, "kotlin")

        // Бизнес-логика оркестрации (например, форматирование ответа)
        return if (aiResponse.bugsFound()) { // Обрати внимание: у рекордов геттеры без 'get', просто bugsFound()
            "⚠️Warning! Найдены ошибки:\n\n${aiResponse.review()}"
        } else {
            "✅OK. Комментарий:\n\n${aiResponse.review()}"
        }
    }
}