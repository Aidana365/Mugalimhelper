package com.example.mugalimhelper.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// LLM API-дан күтілетін жауаптың құрылымы
data class GenerateResponse(
    val result: String
)

// LLM API-ға жіберілетін сұраныстың құрылымы
data class GenerateRequest(
    val prompt: String
)

interface AIService {
    @POST("v1/generate") // API endpoint-ін өзгертіңіз
    suspend fun generateContent(
        @Body request: GenerateRequest,
        @Header("Authorization") apiKey: String = "Bearer YOUR_API_KEY" // Кілтті осы жерде сақтамаңыз!
    ): GenerateResponse

    companion object {
        private const val BASE_URL = "https://api.examplellm.com/" // Өз API URL-ыңызбен алмастырыңыз

        fun create(): AIService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(AIService::class.java)
        }
    }
}