package com.seva.scripture.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.seva.scripture.AppContainer
import com.seva.scripture.ui.viewmodel.ScriptureViewModel
import com.seva.scripture.ui.viewmodel.ScriptureViewModelFactory
import com.seva.scripture.util.ShareUtils
import kotlinx.coroutines.delay

private object Route {
    const val Splash = "splash"
    const val ScriptureSelection = "scripture_selection"
    const val ChapterList = "chapter_list"
    const val Chapter = "chapter/{chapter}" // verses list
    const val Verse = "verse/{chapter}/{verse}" // verse detail
    const val Bookmarks = "bookmarks"
    const val Search = "search"
    const val Settings = "settings"
}

@Composable
fun ScriptureNavHost(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    val vm: ScriptureViewModel = viewModel(factory = ScriptureViewModelFactory(appContainer))

    NavHost(navController = navController, startDestination = Route.Splash) {
        composable(Route.Splash) {
            SplashScreen {
                navController.navigate(Route.ScriptureSelection) {
                    popUpTo(Route.Splash) { inclusive = true }
                }
            }
        }
        composable(Route.ScriptureSelection) {
            ScriptureSelectionScreen(
                vm = vm,
                onScriptureSelected = { id ->
                    vm.selectScripture(id)
                    navController.navigate(Route.ChapterList)
                },
                onSettings = { navController.navigate(Route.Settings) }
            )
        }
        composable(Route.ChapterList) { // Old Home
            ChapterListScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onChapter = { navController.navigate("chapter/$it") },
                onBookmarks = { navController.navigate(Route.Bookmarks) },
                onSearch = { navController.navigate(Route.Search) },
                onSettings = { navController.navigate(Route.Settings) }
            )
        }
        composable(
            route = Route.Chapter,
            arguments = listOf(navArgument("chapter") { type = NavType.IntType })
        ) { backStackEntry ->
            val chapter = backStackEntry.arguments?.getInt("chapter") ?: 1
            VerseListScreen( // Old ChapterScreen
                vm = vm,
                chapterNumber = chapter,
                onBack = { navController.popBackStack() },
                onVerse = { c, v -> navController.navigate("verse/$c/$v") }
            )
        }
        composable(
            route = Route.Verse,
            arguments = listOf(
                navArgument("chapter") { type = NavType.IntType },
                navArgument("verse") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val chapter = backStackEntry.arguments?.getInt("chapter") ?: 1
            val verse = backStackEntry.arguments?.getInt("verse") ?: 1
            VerseDetailScreen(vm = vm, chapter = chapter, verse = verse, onBack = { navController.popBackStack() })
        }
        composable(Route.Bookmarks) {
            BookmarksScreen(vm = vm, onBack = { navController.popBackStack() }) { c, v ->
                navController.navigate("verse/$c/$v")
            }
        }
        composable(Route.Search) {
            SearchScreen(vm = vm, onBack = { navController.popBackStack() }) { c, v ->
                navController.navigate("verse/$c/$v")
            }
        }
        composable(Route.Settings) {
            SettingsScreen(vm = vm, onBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1200)
        onDone()
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ॐ", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sanatana Dharma", style = MaterialTheme.typography.headlineMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScriptureSelectionScreen(
    vm: ScriptureViewModel,
    onScriptureSelected: (String) -> Unit,
    onSettings: () -> Unit
) {
    val scriptures by vm.scriptures.collectAsState()
    val initStatus by vm.initStatus.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library") },
                actions = {
                    IconButton(onClick = onSettings) { Icon(Icons.Filled.Settings, contentDescription = null) }
                }
            )
        }
    ) { padding ->
        if (scriptures.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(initStatus)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                item {
                    Text(
                        "Sacred Texts",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(scriptures) { scripture ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onScriptureSelected(scripture.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = scripture.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = scripture.description,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapterListScreen(
    vm: ScriptureViewModel,
    onBack: () -> Unit,
    onChapter: (Int) -> Unit,
    onBookmarks: () -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit
) {
    val chapters by vm.chapters.collectAsState()
    val scriptureId by vm.currentScriptureId.collectAsState()
    // Simple mapping for display purposes - ideally these should come from the DB/Model
    val title = if (scriptureId.contains("gita")) "Bhagavad Gita" else "Hanuman Chalisa"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                },
                actions = {
                    IconButton(onClick = onSearch) { Icon(Icons.Filled.Search, contentDescription = null) }
                    IconButton(onClick = onBookmarks) { Icon(Icons.Filled.Star, contentDescription = null) }
                }
            )
        }
    ) { padding ->
        if (chapters.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(chapters) { chapter ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChapter(chapter.chapterNumber) }
                            .padding(horizontal = 12.dp),
                        headlineContent = { Text("Chapter ${chapter.chapterNumber}: ${chapter.titleEnglish}") },
                        supportingContent = { Text(chapter.theme) },
                        overlineContent = { Text(chapter.titleSanskrit) },
                        trailingContent = { Text("${chapter.verseCount} verses") }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerseListScreen(
    vm: ScriptureViewModel,
    chapterNumber: Int,
    onBack: () -> Unit,
    onVerse: (Int, Int) -> Unit
) {
    LaunchedEffect(chapterNumber) { vm.selectChapter(chapterNumber) }
    val verses by vm.verses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chapter $chapterNumber") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(verses) { verse ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVerse(verse.chapterNumber, verse.verseNumber) }
                        .padding(horizontal = 12.dp),
                    headlineContent = { Text("${verse.chapterNumber}.${verse.verseNumber}") },
                    supportingContent = { Text(verse.sanskrit, maxLines = 2) },
                    overlineContent = { Text(verse.transliteration, maxLines = 2) }
                )
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerseDetailScreen(vm: ScriptureViewModel, chapter: Int, verse: Int, onBack: () -> Unit) {
    val detail by vm.currentVerse.collectAsState()
    val settings by vm.settings.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(chapter, verse) { vm.selectVerse(chapter, verse) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ch $chapter, Verse $verse") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    val isBookmarked = detail?.bookmarked == true
                    IconButton(onClick = { vm.toggleBookmark(chapter, verse) }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        val verseDetail = detail
        if (verseDetail == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Sanskrit Section
                item {
                    Text(
                        text = "॥ श्लोक ॥",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = verseDetail.sanskrit,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            lineHeight = 36.sp,
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Transliteration Section
                if (settings.transliterationVisible) {
                    item {
                        Text(
                            text = "Transliteration",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = verseDetail.transliteration,
                            style = MaterialTheme.typography.bodyLarge,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 26.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                }

                // Meaning Section
                item {
                    Text(
                        text = "Meaning",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = verseDetail.simpleMeaning,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 28.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Philosophical Note Section
                if (settings.philosophicalVisible && verseDetail.philosophicalNote.isNotBlank()) {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        Text(
                            text = "Philosophical Insight",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = verseDetail.philosophicalNote,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 24.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                // Share Section
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val payload = "Bhagavad Gita ${chapter}.${verse}\n\n${verseDetail.sanskrit}\n\n${verseDetail.simpleMeaning}"
                                ShareUtils.shareText(context, payload)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share Text")
                        }
                        OutlinedButton(
                            onClick = {
                                ShareUtils.shareAsImage(
                                    context = context,
                                    title = "Bhagavad Gita ${chapter}.${verse}",
                                    sanskrit = verseDetail.sanskrit,
                                    meaning = verseDetail.simpleMeaning
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share Image")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(vm: ScriptureViewModel, onBack: () -> Unit, onVerse: (Int, Int) -> Unit) {
    val results by vm.searchResults.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Search") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
            }
        )
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = query,
                onValueChange = {
                    query = it
                    vm.setSearchQuery(it)
                },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                placeholder = { Text("Search chapter:verse or keyword") }
            )
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(results) { verse ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onVerse(verse.chapterNumber, verse.verseNumber) }
                            .padding(horizontal = 12.dp),
                        headlineContent = { Text("${verse.chapterNumber}.${verse.verseNumber}") },
                        supportingContent = { Text(verse.sanskrit, maxLines = 2) },
                        overlineContent = { Text(verse.transliteration, maxLines = 2) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookmarksScreen(vm: ScriptureViewModel, onBack: () -> Unit, onVerse: (Int, Int) -> Unit) {
    val bookmarks by vm.bookmarks.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Bookmarked Shlokas") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
            }
        )
    }) { padding ->
        LazyColumn(contentPadding = padding) {
            items(bookmarks) { verse ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVerse(verse.chapterNumber, verse.verseNumber) }
                        .padding(horizontal = 12.dp),
                    headlineContent = { Text("${verse.chapterNumber}.${verse.verseNumber}") },
                    supportingContent = { Text(verse.sanskrit, maxLines = 2) }
                )
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(vm: ScriptureViewModel, onBack: () -> Unit) {
    val settings by vm.settings.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
            }
        )
    }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                ListItem(
                    headlineContent = { Text("Language") },
                    supportingContent = { Text("English / Hindi") },
                    trailingContent = {
                        Text(
                            text = settings.languageCode.uppercase(),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                TextButton(onClick = {
                    vm.setLanguage(if (settings.languageCode == "en") "hi" else "en")
                }, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text("Toggle language")
                }
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text("Font Scale") },
                    supportingContent = { Text("${settings.fontScale}x") },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                TextButton(onClick = {
                    val next = when (settings.fontScale) {
                        0.9f -> 1.0f
                        1.0f -> 1.1f
                        else -> 0.9f
                    }
                    vm.setFontScale(next)
                }, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text("Cycle font size")
                }
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text("Dark mode") },
                    trailingContent = {
                        Switch(
                            checked = settings.darkMode,
                            onCheckedChange = { vm.setDarkMode(it) }
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Show transliteration") },
                    trailingContent = {
                        Switch(
                            checked = settings.transliterationVisible,
                            onCheckedChange = { vm.setTransliteration(it) }
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Show philosophical note") },
                    trailingContent = {
                        Switch(
                            checked = settings.philosophicalVisible,
                            onCheckedChange = { vm.setPhilosophical(it) }
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Daily shloka notification") },
                    trailingContent = {
                        Switch(
                            checked = settings.dailyNotificationEnabled,
                            onCheckedChange = { vm.setDailyNotification(it) }
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}
