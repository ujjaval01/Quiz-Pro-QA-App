package com.uv.questionsanswers

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import java.util.UUID

data class Question(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0
)

data class TestSeries(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val questions: List<Question> = emptyList(),
    val durationMinutes: Int = 5,
    @get:PropertyName("isPublished")
    @set:PropertyName("isPublished")
    var isPublished: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserSubmission(
    val id: String = UUID.randomUUID().toString(),
    val username: String = "",
    val testSeriesId: String = "",
    val testTitle: String = "",
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val score: Int = 0,
    @get:PropertyName("isResultPublished")
    @set:PropertyName("isResultPublished")
    var isResultPublished: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

object QuizRepository {
    private val db = Firebase.firestore.apply {
        firestoreSettings = firestoreSettings {
            setLocalCacheSettings(persistentCacheSettings {})
        }
    }
    val testSeriesList = mutableStateListOf<TestSeries>()
    val submissions = mutableStateListOf<UserSubmission>()

    init {
        listenToTestSeries()
        listenToSubmissions()
    }

    private fun listenToTestSeries() {
        db.collection("testSeries")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.toObjects<TestSeries>()
                    testSeriesList.clear()
                    testSeriesList.addAll(list)
                }
            }
    }

    private fun listenToSubmissions() {
        db.collection("submissions")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.toObjects<UserSubmission>()
                    submissions.clear()
                    submissions.addAll(list)
                }
            }
    }

    fun addTestSeries(title: String, duration: Int) {
        val series = TestSeries(title = title, durationMinutes = duration)
        db.collection("testSeries").document(series.id).set(series)
    }

    fun updateTestSeries(updated: TestSeries) {
        db.collection("testSeries").document(updated.id).set(updated)
    }

    fun deleteTestSeries(id: String) {
        db.collection("testSeries").document(id).delete()
    }

    fun publishTestSeries(id: String) {
        val series = testSeriesList.find { it.id == id } ?: return
        val updated = series.copy(isPublished = true)
        db.collection("testSeries").document(id).set(updated)
    }

    fun submitTest(username: String, testSeries: TestSeries, selectedAnswers: Map<String, Int>) {
        var correct = 0
        testSeries.questions.forEach { q ->
            if (selectedAnswers[q.id] == q.correctOptionIndex) {
                correct++
            }
        }
        val submission = UserSubmission(
            username = username,
            testSeriesId = testSeries.id,
            testTitle = testSeries.title,
            selectedAnswers = selectedAnswers,
            totalQuestions = testSeries.questions.size,
            correctAnswers = correct,
            score = correct,
            isResultPublished = false
        )
        db.collection("submissions").document(submission.id).set(submission)
    }

    fun publishAllResultsForTest(testSeriesId: String) {
        db.collection("submissions")
            .whereEqualTo("testSeriesId", testSeriesId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    doc.reference.update("isResultPublished", true)
                }
            }
    }
}
