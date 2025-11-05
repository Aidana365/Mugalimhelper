package com.example.mugalimhelper.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow // Осы импортты қосыңыз

@Dao
interface LessonDao {
    @Insert
    suspend fun insertLesson(lesson: LessonTopic)

    // 'suspend' кілт сөзін алып тастап, Flow<List<...>> деп өзгертеміз
    @Query("SELECT * FROM lesson_topics ORDER BY timestamp DESC")
    fun getAllLessons(): Flow<List<LessonTopic>>
}
