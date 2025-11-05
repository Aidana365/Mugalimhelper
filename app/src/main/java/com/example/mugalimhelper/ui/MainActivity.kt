package com.example.mugalimhelper.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.mugalimhelper.data.AppDatabase
import com.example.mugalimhelper.data.LessonTopic
import com.example.mugalimhelper.ui.theme.MugalimhelperTheme // Theme импорты қосылды
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Room деректер базасын жасау
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "lesson_db"
        ).build()

        val dao = db.lessonDao()

        setContent {
            // Қосымшаның темасын қолданамыз
            MugalimhelperTheme {
                val coroutineScope = rememberCoroutineScope()

                // Айнымалылар (UI күйі)
                var topic by remember { mutableStateOf("") }
                var grade by remember { mutableStateOf("") }
                var type by remember { mutableStateOf("") }

                // --- ТҮЗЕТІЛГЕН БӨЛІК ---
                // Flow-ды State-ке айналдырамыз.
                // Дерекқор өзгергенде, 'lessons' автоматты түрде жаңарады.
                val lessons by dao.getAllLessons().collectAsState(initial = emptyList())

                // LaunchedEffect енді қажет емес, себебі collectAsState бәрін өзі істейді.

                // Негізгі экран
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Mugalim Helper") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(16.dp)
                            .fillMaxSize() // Column толық экранды алуы үшін
                    ) {
                        OutlinedTextField(
                            value = topic,
                            onValueChange = { topic = it },
                            label = { Text("Сабақ тақырыбы") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = grade,
                            onValueChange = { grade = it },
                            label = { Text("Сынып") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = type,
                            onValueChange = { type = it },
                            label = { Text("Сабақ түрі") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // Енгізу өрістері бос емес екенін тексеруге болады
                                if (topic.isNotBlank() && grade.isNotBlank() && type.isNotBlank()) {
                                    coroutineScope.launch {
                                        val newLesson = LessonTopic(
                                            topic = topic,
                                            grade = grade,
                                            lessonType = type,
                                            generatedContent = "AI мазмұн кейін қосылады"
                                        )
                                        dao.insertLesson(newLesson)
                                        // Енді тізімді қолмен жаңартудың қажеті жоқ
                                        // lessons = dao.getAllLessons() <-- Бұл жол алынып тасталды

                                        // Енгізу өрістерін тазалау
                                        topic = ""
                                        grade = ""
                                        type = ""
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Сабақты сақтау")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Сақталған сабақтарды көрсету
                        LazyColumn {
                            items(lessons) { lesson ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("📘 Тақырып: ${lesson.topic}")
                                        Text("🏫 Сынып: ${lesson.grade}")
                                        Text("📖 Түрі: ${lesson.lessonType}")
                                        // Мазмұн өте ұзын болуы мүмкін, сондықтан бірнеше жолға шектеу қоюға болады
                                        Text(
                                            "🧠 Мазмұн: ${lesson.generatedContent}",
                                            maxLines = 3
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}