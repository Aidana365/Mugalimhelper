package com.example.mugalimhelper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mugalimhelper.data.AppDatabase
import com.example.mugalimhelper.data.LessonTopic
import com.example.mugalimhelper.network.AIService
import com.example.mugalimhelper.repo.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel-дегі UI күйін басқаратын data class
data class LessonUiState(
    val topic: String = "",
    val grade: String = "",
    val lessonType: String = "",
    val generatedResult: String = "",
    val isLoading: Boolean = false
)

class LessonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LessonRepository

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val lessonDao = AppDatabase.getDatabase(application).lessonDao()
        val aiService = AIService.create()
        repository = LessonRepository(lessonDao, aiService)
    }

    fun onTopicChange(newTopic: String) {
        _uiState.update { it.copy(topic = newTopic) }
    }

    fun onGradeChange(newGrade: String) {
        _uiState.update { it.copy(grade = newGrade) }
    }

    fun onLessonTypeChange(newLessonType: String) {
        _uiState.update { it.copy(lessonType = newLessonType) }
    }

    fun generateLessonPlan() {
        val currentState = _uiState.value
        if (currentState.topic.isBlank() || currentState.grade.isBlank() || currentState.lessonType.isBlank()) {
            _uiState.update { it.copy(generatedResult = "Барлық жолдарды толтырыңыз!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generatedResult = "") }
            val content = repository.generateLessonContent(
                topic = currentState.topic,
                grade = currentState.grade,
                lessonType = currentState.lessonType
            )
            _uiState.update { it.copy(generatedResult = content, isLoading = false) }

            // Нәтижені дерекқорға сақтау
            val newLesson = LessonTopic(
                topic = currentState.topic,
                grade = currentState.grade,
                lessonType = currentState.lessonType,
                generatedContent = content
            )
            repository.saveLesson(newLesson)
        }
    }
}