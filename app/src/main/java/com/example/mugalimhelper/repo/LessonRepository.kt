package com.example.mugalimhelper.repo

import com.example.mugalimhelper.data.LessonDao
import com.example.mugalimhelper.data.LessonTopic
import com.example.mugalimhelper.network.AIService
import com.example.mugalimhelper.network.GenerateRequest
import kotlinx.coroutines.flow.Flow

class LessonRepository(
    private val lessonDao: LessonDao,
    private val aiService: AIService
) {
    // Енді бұл жол дұрыс, себебі lessonDao.getAllLessons() өзі Flow қайтарады
    fun getAllLessons(): Flow<List<LessonTopic>> = lessonDao.getAllLessons()

    suspend fun generateLessonContent(topic: String, grade: String, lessonType: String): String {
        return try {
            // API-ға сұраныс жасап көру
            val request = GenerateRequest(prompt = "Мен үшін $grade сыныбына '$lessonType' сабақ түрі бойынша '$topic' тақырыбына сабақ жоспарын жаса.")
            val response = aiService.generateContent(request)
            response.result
        } catch (e: Exception) {
            // Егер API қатесі болса, жергілікті шаблонды қайтару
            // Қатені логқа шығару пайдалы болуы мүмкін
            // Log.e("LessonRepository", "API error: ${e.message}")
            getLocalTemplate(topic, grade, lessonType)
        }
    }

    suspend fun saveLesson(lessonTopic: LessonTopic) {
        lessonDao.insertLesson(lessonTopic)
    }

    private fun getLocalTemplate(topic: String, grade: String, lessonType: String): String {
        // API-сыз жағдайда қолданылатын қарапайым шаблон
        return """
            Тақырып: $topic
            Сынып: $grade
            Сабақ түрі: $lessonType

            Жоспар (локалды шаблон):
            1. Ұйымдастыру кезеңі.
            2. Үй тапсырмасын тексеру.
            3. Жаңа тақырыпты түсіндіру.
            4. Бекіту сұрақтары.
            5. Үйге тапсырма беру.
            
            (Бұл хабарлама желіге қосылым болмағандықтан немесе API қатесіне байланысты көрсетілді)
        """.trimIndent()
    }
}