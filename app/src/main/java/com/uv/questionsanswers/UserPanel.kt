package com.uv.questionsanswers

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uv.questionsanswers.ui.theme.NeuBackground
import com.uv.questionsanswers.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPanel(username: String, onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedTestSeries by remember { mutableStateOf<TestSeries?>(null) }
    var showInstructions by remember { mutableStateOf(false) }
    var reviewingSubmission by remember { mutableStateOf<UserSubmission?>(null) }
    
    val userSubmissions = QuizRepository.submissions.filter { it.username == username }

    PremiumScreen {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            if (reviewingSubmission != null) "Review Result"
                            else if (selectedTestSeries == null) "Dashboard" 
                            else selectedTestSeries!!.title, 
                            fontWeight = FontWeight.ExtraBold
                        ) 
                    },
                    navigationIcon = {
                        if (reviewingSubmission != null) {
                            IconButton(onClick = { reviewingSubmission = null }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                        } else if (selectedTestSeries != null) {
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
                if (reviewingSubmission != null) {
                    val series = QuizRepository.testSeriesList.find { it.id == reviewingSubmission!!.testSeriesId }
                    if (series != null) {
                        ReviewSubmissionScreen(submission = reviewingSubmission!!, testSeries = series)
                    } else {
                        reviewingSubmission = null
                    }
                } else if (selectedTestSeries == null) {
                    // Welcome Header
                    Box(modifier = Modifier.padding(24.dp)) {
                        Column {
                            Text("Hello,", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                            Text(username, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .height(56.dp)
                            .neumorphic(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(NeuBackground)
                            .padding(4.dp)
                    ) {
                        val transition = updateTransition(targetState = selectedTab, label = "tab")

                        // The Neumorphic Indicator (Behind the text)
                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val tabWidth = maxWidth / 2
                            val offset by transition.animateDp(label = "offset") { if (it == 0) 0.dp else tabWidth }
                            
                            Box(
                                modifier = Modifier
                                    .offset(x = offset)
                                    .width(tabWidth)
                                    .fillMaxHeight()
                                    .neumorphic(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(NeuBackground)
                            )
                        }

                        // The Tabs (Above the indicator)
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { selectedTab = 0 },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Available",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { selectedTab = 1 },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "My Results",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
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
                            UserResultsList(userSubmissions) { sub ->
                                reviewingSubmission = sub
                            }
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
}

@Composable
fun InstructionsScreen(testSeries: TestSeries, onStart: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(100.dp).neumorphic(shape = RoundedCornerShape(28.dp)).clip(RoundedCornerShape(28.dp)).background(NeuBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
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
        Box(modifier = Modifier.size(40.dp).neumorphic(elevation = 2.dp, shape = CircleShape).clip(CircleShape).background(NeuBackground), contentAlignment = Alignment.Center) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
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
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        itemsIndexed(tests, key = { _, it -> it.id }) { index, series ->
            StaggeredFadeIn(index) {
                PremiumCard(onClick = { onSelect(series) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(series.title, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                            Text("${series.questions.size} Questions • ${series.durationMinutes} Mins", color = Color.Gray, fontSize = 13.sp)
                        }
                        Box(modifier = Modifier.size(44.dp).neumorphic(elevation = 3.dp, shape = CircleShape).clip(CircleShape).background(NeuBackground), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PlayArrow, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
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
        // Neumorphic Timer Bar
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp).neumorphic(elevation = 2.dp, shape = RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).background(NeuBackground),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
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

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            itemsIndexed(testSeries.questions) { index, question ->
                StaggeredFadeIn(index) {
                    Column {
                        Text(
                            text = "Question ${index + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                        )
                        PremiumCard {
                            Text(question.text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(24.dp))
                            question.options.forEachIndexed { optIndex, option ->
                                val isSelected = selectedAnswers[question.id] == optIndex
                                OptionItem(
                                    text = option,
                                    isSelected = isSelected,
                                    onClick = { selectedAnswers[question.id] = optIndex }
                                )
                                if (optIndex < question.options.size - 1) Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            PremiumButton(
                text = "SUBMIT QUIZ",
                onClick = { 
                    QuizRepository.submitTest(username, testSeries, selectedAnswers)
                    isSubmitted = true
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun OptionItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neumorphic(elevation = if(isSelected) 1.dp else 2.dp, isPressed = isSelected)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else NeuBackground)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if(isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray)
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
        Text("🎉", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Quiz Completed!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text("You've done a great job, $username", color = Color.Gray)
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Box(
            modifier = Modifier.size(180.dp).neumorphic(elevation = 4.dp, shape = CircleShape).clip(CircleShape).background(NeuBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier.fillMaxSize(0.85f),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 10.dp,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$percentage%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Text("SCORE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ScoreMiniCard("Correct", "$correctCount", SuccessGreen, Modifier.weight(1f))
            ScoreMiniCard("Total", "${testSeries.questions.size}", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        PremiumButton(text = "BACK TO DASHBOARD", onClick = onComplete, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ScoreMiniCard(label: String, value: String, color: Color, modifier: Modifier) {
    PremiumCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}

@Composable
fun UserResultsList(submissions: List<UserSubmission>, onClick: (UserSubmission) -> Unit) {
    if (submissions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No results yet.", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        itemsIndexed(submissions.reversed()) { index, sub ->
            StaggeredFadeIn(index) {
                PremiumCard(onClick = if (sub.isResultPublished) { { onClick(sub) } } else null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sub.testTitle, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            Text(formatDate(sub.timestamp), fontSize = 12.sp, color = Color.Gray)
                        }
                        if (!sub.isResultPublished) {
                            Surface(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp)) {
                                Text("PENDING", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${sub.correctAnswers}/${sub.totalQuestions}", fontWeight = FontWeight.ExtraBold, color = SuccessGreen, fontSize = 20.sp)
                                Text("Review", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewSubmissionScreen(submission: UserSubmission, testSeries: TestSeries) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            PremiumCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Performance", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text("${submission.correctAnswers}/${submission.totalQuestions} Correct", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                    val percentage = (submission.correctAnswers * 100) / submission.totalQuestions
                    Text("$percentage%", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                }
            }
        }

        itemsIndexed(testSeries.questions) { index, question ->
            // Use Number to handle Firestore Int/Long type conversion issues
            val userSelection = (submission.selectedAnswers[question.id] as? Number)?.toInt()
            val isCorrect = userSelection == question.correctOptionIndex
            val isSkipped = userSelection == null

            StaggeredFadeIn(index) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Question ${index + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isSkipped) {
                            Surface(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp)) {
                                Text("SKIPPED", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        } else {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel, 
                                contentDescription = null, 
                                tint = if (isCorrect) SuccessGreen else MaterialTheme.colorScheme.error, 
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    PremiumCard {
                        Text(question.text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        question.options.forEachIndexed { optIndex, option ->
                            val isUserSelected = userSelection == optIndex
                            val isCorrectOption = optIndex == question.correctOptionIndex
                            
                            val statusColor = when {
                                isCorrectOption -> SuccessGreen
                                isUserSelected && !isCorrect -> MaterialTheme.colorScheme.error
                                else -> Color.Gray.copy(alpha = 0.3f)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .neumorphic(elevation = if(isUserSelected || isCorrectOption) 1.dp else 2.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if(isUserSelected || isCorrectOption) statusColor.copy(alpha = 0.05f) else NeuBackground)
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when {
                                            isCorrectOption -> Icons.Default.CheckCircle
                                            isUserSelected -> Icons.Default.Cancel
                                            else -> Icons.Default.RadioButtonUnchecked
                                        },
                                        contentDescription = null,
                                        tint = statusColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = option, 
                                        fontWeight = if (isUserSelected || isCorrectOption) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isUserSelected || isCorrectOption) Color.DarkGray else Color.Gray
                                    )
                                }
                            }
                            if (optIndex < question.options.size - 1) Spacer(modifier = Modifier.height(12.dp))
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
