package com.seva.scripture.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
    const val Home = "home"
    const val Chapter = "chapter/{chapter}"
    const val Verse = "verse/{chapter}/{verse}"
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
                navController.navigate(Route.Home) {
                    popUpTo(Route.Splash) { inclusive = true }
                }
            }
        }
        composable(Route.Home) {
            HomeScreen(
                vm = vm,
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
            ChapterScreen(
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
            VerseScreen(vm = vm, chapter = chapter, verse = verse, onBack = { navController.popBackStack() })
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
        Text("ॐ", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text("Bhagavad Gita Seva", style = MaterialTheme.typography.titleLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    vm: ScriptureViewModel,
    onChapter: (Int) -> Unit,
    onBookmarks: () -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit
) {
    val chapters by vm.chapters.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bhagavad Gita") },
                actions = {
                    IconButton(onClick = onSearch) { Icon(Icons.Filled.Search, contentDescription = null) }
                    IconButton(onClick = onBookmarks) { Icon(Icons.Filled.Star, contentDescription = null) }
                    IconButton(onClick = onSettings) { Icon(Icons.Filled.Settings, contentDescription = null) }
                }
            )
        }
    ) { padding ->
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
                    trailingContent = { Text("${chapter.verseCount}") }
                )
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapterScreen(
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
private fun VerseScreen(vm: ScriptureViewModel, chapter: Int, verse: Int, onBack: () -> Unit) {
    val detail by vm.currentVerse.collectAsState()
    val settings by vm.settings.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(chapter, verse) { vm.selectVerse(chapter, verse) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$chapter.$verse") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
                },
                actions = {
                    IconButton(onClick = { vm.toggleBookmark(chapter, verse) }) {
                        Icon(Icons.Filled.Star, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        val verseDetail = detail
        if (verseDetail == null) {
            Column(modifier = Modifier.padding(padding).padding(20.dp)) {
                Text("No verse content found.")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = verseDetail.sanskrit,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                if (settings.transliterationVisible) {
                    Text(text = verseDetail.transliteration, style = MaterialTheme.typography.bodyLarge)
                }
                Text(text = verseDetail.simpleMeaning, style = MaterialTheme.typography.bodyLarge)
                if (settings.philosophicalVisible) {
                    Text(text = verseDetail.philosophicalNote, style = MaterialTheme.typography.bodyMedium)
                }
                Card {
                    Column(modifier = Modifier.padding(8.dp)) {
                        TextButton(onClick = {
                            val payload = "Bhagavad Gita ${chapter}.${verse}\n\n${verseDetail.sanskrit}\n\n${verseDetail.simpleMeaning}"
                            ShareUtils.shareText(context, payload)
                        }) {
                            Text("Share as text")
                        }
                        TextButton(onClick = {
                            ShareUtils.shareAsImage(
                                context = context,
                                title = "Bhagavad Gita ${chapter}.${verse}",
                                sanskrit = verseDetail.sanskrit,
                                meaning = verseDetail.simpleMeaning
                            )
                        }) {
                            Text("Share as image")
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
