package com.example.mugalimhelper.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_topics")
data class LessonTopic(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val topic: String, // Сабақ тақырыбы
    val grade: String, // Сынып
    val lessonType: String, // Сабақ түрі
    val generatedContent: String, // Генерацияланған мазмұн
    val timestamp: Long = System.currentTimeMillis() // Жасалған уақыты
)