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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPanel(username: String, onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedTestSeries by remember { mutableStateOf<TestSeries?>(null) }
    var showInstructions by remember { mutableStateOf(false) }
    
    val userSubmissions = QuizRepository.submissions.filter { it.username == username }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        if (selectedTestSeries == null) "Dashboard" else selectedTestSeries!!.title, 
                        fontWeight = FontWeight.ExtraBold
                    ) 
                },
                navigationIcon = {
                    if (selectedTestSeries != null) {
                        IconButton(onClick = { selectedTestSeries = null; showInstructions = false }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (selectedTestSeries == null) {
                // Welcome Header
                Box(modifier = Modifier.padding(24.dp)) {
                    Column {
                        Text("Hello,", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Text(username, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        )
                    }
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, modifier = Modifier.height(48.dp)) {
                        Text("Available Tests", fontWeight = FontWeight.Bold)
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, modifier = Modifier.height(48.dp)) {
                        Text("My Results", fontWeight = FontWeight.Bold)
                    }
                }

                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "userTabAnim"
                ) { tab ->
                    if (tab == 0) {
                        AvailableTestsList(username) { series ->
                            selectedTestSeries = series
                            showInstructions = true
                        }
                    } else {
                        UserResultsList(userSubmissions)
                    }
                }
            } else {
                if (showInstructions) {
                    InstructionsScreen(
                        testSeries = selectedTestSeries!!,
                        onStart = { showInstructions = false },
                        onBack = { selectedTestSeries = null }
                    )
                } else {
                    QuizView(username, selectedTestSeries!!) {
                        selectedTestSeries = null
                        selectedTab = 1
                    }
                }
            }
        }
    }
}

@Composable
fun InstructionsScreen(testSeries: TestSeries, onStart: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text("Test Instructions", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text("Please read carefully before starting", color = Color.Gray)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PremiumCard(modifier = Modifier.fillMaxWidth()) {
            InstructionItem("Duration", "${testSeries.durationMinutes} Minutes", Icons.Default.Timer)
            InstructionItem("Questions", "${testSeries.questions.size} Questions", Icons.Default.QuestionMark)
            InstructionItem("Guidelines", "Do not switch tabs or exit.", Icons.Default.Lock)
            InstructionItem("Safety", "Test will auto-submit on timeout.", Icons.Default.CloudDone)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        PremiumButton(
            text = "START TEST",
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        )
        TextButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
            Text("NOT NOW", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InstructionItem(label: String, value: String, icon: ImageVector) {
    Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.size(36.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
fun AvailableTestsList(username: String, onSelect: (TestSeries) -> Unit) {
    val submittedTestIds = QuizRepository.submissions
        .filter { it.username == username }
        .map { it.testSeriesId }
        .toSet()

    val tests = QuizRepository.testSeriesList.filter { it.isPublished && it.id !in submittedTestIds }

    if (tests.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Celebration, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))
                Text("You're all caught up!", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(tests, key = { _, it -> it.id }) { index, series ->
            StaggeredFadeIn(index) {
                PremiumCard(onClick = { onSelect(series) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(series.title, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                            Text("${series.questions.size} Questions • ${series.durationMinutes} Mins", color = Color.Gray, fontSize = 13.sp)
                        }
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizView(username: String, testSeries: TestSeries, onComplete: () -> Unit) {
    val selectedAnswers = remember { mutableStateMapOf<String, Int>() }
    var timeLeftSeconds by remember { mutableIntStateOf(testSeries.durationMinutes * 60) }
    var isSubmitted by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = timeLeftSeconds) {
        if (timeLeftSeconds > 0 && !isSubmitted) {
            kotlinx.coroutines.delay(1000L)
            timeLeftSeconds--
        } else if (timeLeftSeconds <= 0 && !isSubmitted) {
            QuizRepository.submitTest(username, testSeries, selectedAnswers)
            isSubmitted = true
        }
    }

    if (isSubmitted) {
        ResultView(username, testSeries, selectedAnswers, onComplete)
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Premium Timer Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), // Transparent timer bar
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val minutes = timeLeftSeconds / 60
                    val seconds = timeLeftSeconds % 60
                    val color = if (timeLeftSeconds < 60) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    
                    Icon(Icons.Default.Timer, null, tint = color, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = color
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(testSeries.questions) { index, question ->
                StaggeredFadeIn(index) {
                    Column {
                        Text(
                            text = "Question ${index + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PremiumCard {
                            Text(question.text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            question.options.forEachIndexed { optIndex, option ->
                                val isSelected = selectedAnswers[question.id] == optIndex
                                OptionItem(
                                    text = option,
                                    isSelected = isSelected,
                                    onClick = { selectedAnswers[question.id] = optIndex }
                                )
                                if (optIndex < question.options.size - 1) Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = MaterialTheme.colorScheme.surface) {
            PremiumButton(
                text = "SUBMIT QUIZ",
                onClick = { 
                    QuizRepository.submitTest(username, testSeries, selectedAnswers)
                    isSubmitted = true
                },
                modifier = Modifier.fillMaxWidth().padding(24.dp)
            )
        }
    }
}

@Composable
fun OptionItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
    val borderColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
        color = backgroundColor
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@Composable
fun ResultView(username: String, testSeries: TestSeries, answers: Map<String, Int>, onComplete: () -> Unit) {
    val correctCount = testSeries.questions.count { answers[it.id] == it.correctOptionIndex }
    val percentage = (correctCount * 100) / testSeries.questions.size

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Celebration Animation Placeholder
        Text("🎉", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Quiz Completed!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text("You've done a great job, $username", color = Color.Gray)
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
            CircularProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 12.dp,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$percentage%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                Text("SCORE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ScoreMiniCard("Correct", "$correctCount", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
            ScoreMiniCard("Total", "${testSeries.questions.size}", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        PremiumButton(text = "DONE", onClick = onComplete, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ScoreMiniCard(label: String, value: String, color: Color, modifier: Modifier) {
    PremiumCard(modifier = modifier, containerColor = color.copy(alpha = 0.05f)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}

@Composable
fun UserResultsList(submissions: List<UserSubmission>) {
    if (submissions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No results yet.", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(submissions.reversed()) { index, sub ->
            StaggeredFadeIn(index) {
                PremiumCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sub.testTitle, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            Text(formatDate(sub.timestamp), fontSize = 12.sp, color = Color.Gray)
                        }
                        if (!sub.isResultPublished) {
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
                                Text("PENDING", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        } else {
                            Text("${sub.correctAnswers}/${sub.totalQuestions}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
