package com.uv.questionsanswers

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanel(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddTestDialog by remember { mutableStateOf(false) }
    var editingTestSeries by remember { mutableStateOf<TestSeries?>(null) }
    var editingMetadata by remember { mutableStateOf<TestSeries?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Admin Portal", fontWeight = FontWeight.ExtraBold) },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                if (selectedTab == 0 && editingTestSeries == null) {
                    FloatingActionButton(
                        onClick = { showAddTestDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                if (editingTestSeries == null) {
                    // Statistics Section
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard("Tests", QuizRepository.testSeriesList.size.toString(), Modifier.weight(1f))
                        StatCard("Submissions", QuizRepository.submissions.size.toString(), Modifier.weight(1f))
                    }

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = MaterialTheme.colorScheme.primary,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Test Series", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Submissions", fontWeight = FontWeight.Bold) }
                        )
                    }

                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tabAnim"
                    ) { tab ->
                        if (tab == 0) {
                            TestSeriesList(
                                onOpen = { editingTestSeries = it },
                                onEditMetadata = { editingMetadata = it }
                            )
                        } else {
                            AllSubmissionsList()
                        }
                    }
                } else {
                    TestEditor(
                        testSeriesId = editingTestSeries!!.id,
                        onBack = { editingTestSeries = null }
                    )
                }
            }
        }

    if (showAddTestDialog) {
        CreateTestDialog(
            onDismiss = { showAddTestDialog = false },
            onCreated = { title, duration ->
                QuizRepository.addTestSeries(title, duration)
                showAddTestDialog = false
            }
        )
    }

    if (editingMetadata != null) {
        CreateTestDialog(
            initialTitle = editingMetadata!!.title,
            initialDuration = editingMetadata!!.durationMinutes.toString(),
            onDismiss = { editingMetadata = null },
            onCreated = { title, duration ->
                QuizRepository.updateTestSeries(editingMetadata!!.copy(title = title, durationMinutes = duration))
                editingMetadata = null
            }
        )
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun TestSeriesList(onOpen: (TestSeries) -> Unit, onEditMetadata: (TestSeries) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(QuizRepository.testSeriesList, key = { _, it -> it.id }) { index, series ->
            StaggeredFadeIn(index) {
                PremiumCard(onClick = { onOpen(series) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(series.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (series.isPublished) MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (series.isPublished) "PUBLISHED" else "DRAFT",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (series.isPublished) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${series.questions.size} Questions • ${series.durationMinutes} Mins", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        IconButton(onClick = { onEditMetadata(series) }) {
                            Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { QuizRepository.deleteTestSeries(series.id) }) {
                            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestEditor(testSeriesId: String, onBack: () -> Unit) {
    var showQuestionDialog by remember { mutableStateOf(false) }
    var showBulkImportDialog by remember { mutableStateOf(false) }
    var editingQuestion by remember { mutableStateOf<Question?>(null) }
    
    val currentSeries = remember(testSeriesId, QuizRepository.testSeriesList.toList()) { 
        QuizRepository.testSeriesList.find { it.id == testSeriesId }
    }

    if (currentSeries == null) {
        onBack()
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.padding(16.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        }
        
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(currentSeries.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text("${currentSeries.questions.size} Questions", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PremiumButton(
                    text = if (currentSeries.isPublished) "REPUBLISH TEST" else "PUBLISH TEST",
                    onClick = { QuizRepository.publishTestSeries(currentSeries.id) },
                    modifier = Modifier.weight(1f),
                    containerColor = if (currentSeries.isPublished) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                    contentColor = if (currentSeries.isPublished) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                )
                
                OutlinedButton(
                    onClick = { showBulkImportDialog = true },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(PremiumTheme.CornerMedium)
                ) {
                    Icon(Icons.Default.CloudUpload, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("BULK ADD", fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(currentSeries.questions, key = { _, it -> it.id }) { index, question ->
                StaggeredFadeIn(index) {
                    PremiumCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${index + 1}",
                                modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(question.text, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = { editingQuestion = question }) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp))
                            }
                            IconButton(onClick = {
                                val updatedQuestions = currentSeries.questions.filter { it.id != question.id }
                                QuizRepository.updateTestSeries(currentSeries.copy(questions = updatedQuestions))
                            }) {
                                Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = { showQuestionDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(PremiumTheme.CornerMedium)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ADD QUESTION", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showQuestionDialog || editingQuestion != null) {
        QuestionDialog(
            initialQuestion = editingQuestion,
            onDismiss = { 
                showQuestionDialog = false
                editingQuestion = null
            },
            onSave = { q ->
                val updatedQuestions = if (editingQuestion == null) {
                    currentSeries.questions + q
                } else {
                    currentSeries.questions.map { if (it.id == q.id) q else it }
                }
                QuizRepository.updateTestSeries(currentSeries.copy(questions = updatedQuestions))
                showQuestionDialog = false
                editingQuestion = null
            }
        )
    }

    if (showBulkImportDialog) {
        BulkImportDialog(
            onDismiss = { showBulkImportDialog = false },
            onImport = { newQuestions ->
                QuizRepository.updateTestSeries(currentSeries.copy(questions = currentSeries.questions + newQuestions))
                showBulkImportDialog = false
            }
        )
    }
}

@Composable
fun BulkImportDialog(onDismiss: () -> Unit, onImport: (List<Question>) -> Unit) {
    var jsonInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        PremiumCard {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Bulk Import Questions", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text(
                    "Paste your questions in JSON format. Ask me (AI) to format your list if needed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = jsonInput,
                    onValueChange = { jsonInput = it; error = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    placeholder = { Text("[{\"text\": \"...\", \"options\": [...], \"correctOptionIndex\": 0}]") },
                    textStyle = MaterialTheme.typography.bodySmall,
                    shape = RoundedCornerShape(12.dp)
                )

                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    PremiumButton(
                        text = "IMPORT",
                        onClick = {
                            try {
                                val array = JSONArray(jsonInput)
                                val list = mutableListOf<Question>()
                                for (i in 0 until array.length()) {
                                    val obj = array.getJSONObject(i)
                                    val optionsArray = obj.getJSONArray("options")
                                    val options = List(optionsArray.length()) { optionsArray.getString(it) }
                                    list.add(Question(
                                        text = obj.getString("text"),
                                        options = options,
                                        correctOptionIndex = obj.getInt("correctOptionIndex")
                                    ))
                                }
                                onImport(list)
                            } catch (e: Exception) {
                                error = "Invalid JSON format: ${e.message}"
                            }
                        },
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun AllSubmissionsList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(QuizRepository.submissions.reversed()) { index, sub ->
            StaggeredFadeIn(index) {
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), modifier = Modifier.size(48.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(sub.username.take(1).uppercase(), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sub.username, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text(sub.testTitle, fontSize = 12.sp, color = Color.Gray)
                        }
                        Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp)) {
                            Text(
                                text = "${sub.correctAnswers}/${sub.totalQuestions}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                    if (!sub.isResultPublished) {
                        Spacer(modifier = Modifier.height(16.dp))
                        PremiumButton(
                            text = "PUBLISH SCORE",
                            onClick = { QuizRepository.publishAllResultsForTest(sub.testSeriesId) },
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreateTestDialog(
    initialTitle: String = "",
    initialDuration: String = "10",
    onDismiss: () -> Unit,
    onCreated: (String, Int) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var duration by remember { mutableStateOf(initialDuration) }

    Dialog(onDismissRequest = onDismiss) {
        PremiumCard {
            Text(if (initialTitle.isEmpty()) "New Test Series" else "Edit Test Details", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(24.dp))
            PremiumTextField(value = title, onValueChange = { title = it }, label = "Test Title")
            Spacer(modifier = Modifier.height(16.dp))
            PremiumTextField(value = duration, onValueChange = { duration = it }, label = "Duration (Minutes)", keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                PremiumButton(
                    text = "SAVE",
                    onClick = { if(title.isNotBlank()) onCreated(title, duration.toIntOrNull() ?: 10) },
                    modifier = Modifier.weight(1.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDialog(
    initialQuestion: Question? = null,
    onDismiss: () -> Unit,
    onSave: (Question) -> Unit
) {
    var text by remember { mutableStateOf(initialQuestion?.text ?: "") }
    var opt1 by remember { mutableStateOf(initialQuestion?.options?.getOrNull(0) ?: "") }
    var opt2 by remember { mutableStateOf(initialQuestion?.options?.getOrNull(1) ?: "") }
    var opt3 by remember { mutableStateOf(initialQuestion?.options?.getOrNull(2) ?: "") }
    var opt4 by remember { mutableStateOf(initialQuestion?.options?.getOrNull(3) ?: "") }
    var correctIndex by remember { mutableIntStateOf(initialQuestion?.correctOptionIndex ?: 0) }

    Dialog(onDismissRequest = onDismiss) {
        PremiumCard {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = if (initialQuestion == null) "New Question" else "Edit Question", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(24.dp))
                PremiumTextField(value = text, onValueChange = { text = it }, label = "Question Text")
                Spacer(modifier = Modifier.height(20.dp))
                
                val opts = listOf(opt1 to { s:String -> opt1 = s }, opt2 to { s:String -> opt2 = s }, opt3 to { s:String -> opt3 = s }, opt4 to { s:String -> opt4 = s })
                opts.forEachIndexed { index, pair ->
                    PremiumTextField(
                        value = pair.first, onValueChange = pair.second,
                        label = "Option ${index + 1}",
                        trailingIcon = { 
                            RadioButton(
                                selected = correctIndex == index, 
                                onClick = { correctIndex = index },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            ) 
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    PremiumButton(
                        text = "SAVE",
                        onClick = {
                            if (text.isNotBlank() && opt1.isNotBlank()) {
                                onSave(Question(id = initialQuestion?.id ?: java.util.UUID.randomUUID().toString(), text = text, options = listOf(opt1, opt2, opt3, opt4), correctOptionIndex = correctIndex))
                            }
                        },
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        }
    }
}
