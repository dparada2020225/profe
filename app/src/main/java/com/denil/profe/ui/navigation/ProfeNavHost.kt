package com.denil.profe.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.denil.profe.ui.screens.cardeditor.CardEditorScreen
import com.denil.profe.ui.screens.course.CourseScreen
import com.denil.profe.ui.screens.deckdetail.DeckDetailScreen
import com.denil.profe.ui.screens.decks.DeckListScreen
import com.denil.profe.ui.screens.dictionary.DictionaryScreen
import com.denil.profe.ui.screens.home.HomeScreen
import com.denil.profe.ui.screens.lesson.LessonScreen
import com.denil.profe.ui.screens.placement.PlacementScreen
import com.denil.profe.ui.screens.practice.DrillScreen
import com.denil.profe.ui.screens.practice.PracticeScreen
import com.denil.profe.ui.screens.review.ReviewScreen
import com.denil.profe.ui.screens.settings.SettingsScreen
import com.denil.profe.ui.screens.stats.StatsScreen
import com.denil.profe.ui.screens.tutor.ScenarioPickerScreen
import com.denil.profe.ui.screens.tutor.TutorScreen
import com.denil.profe.ui.screens.writing.WritingScreen

object Routes {
    const val HOME = "home"
    const val COURSE = "course"
    const val PRACTICE = "practice"
    const val DECKS = "decks"
    const val SETTINGS = "settings"
    const val STATS = "stats"
    const val PLACEMENT = "placement"
    const val LESSON = "lesson/{unitId}"
    const val DRILL = "drill/{mode}"
    const val DICTATION = "drill/listen"
    const val PRONUNCIATION = "drill/speak"
    const val WRITING = "writing"
    const val DICTIONARY = "dictionary"
    const val TUTOR_PICK = "tutor"
    const val TUTOR = "tutor/{scenario}"
    const val DECK_DETAIL = "deck/{deckId}"
    const val CARD_EDITOR = "deck/{deckId}/card?cardId={cardId}"
    const val REVIEW = "review/{deckId}?course={course}"

    fun lesson(unitId: String) = "lesson/$unitId"
    fun tutor(scenario: String) = "tutor/$scenario"
    fun deckDetail(deckId: Long) = "deck/$deckId"
    fun cardEditor(deckId: Long, cardId: Long? = null) = "deck/$deckId/card?cardId=${cardId ?: -1L}"
    fun review(deckId: Long) = "review/$deckId"
    fun reviewCourse(course: String) = "review/-1?course=" + android.net.Uri.encode(course)

    val topLevel = listOf(HOME, COURSE, PRACTICE, DECKS, SETTINGS)
}

@Composable
fun ProfeNavHost(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val navigateTopLevel: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    val open: (String) -> Unit = { route -> navController.navigate(route) { launchSingleTop = true } }
    val back: () -> Unit = { navController.popBackStack() }
    val openSettings: () -> Unit = { navigateTopLevel(Routes.SETTINGS) }

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                currentRoute = currentRoute,
                onNavigateTopLevel = navigateTopLevel,
                onOpenLesson = { open(Routes.lesson(it)) },
                onPlacement = { open(Routes.PLACEMENT) },
                onStudyVocab = { open(Routes.review(com.denil.profe.ui.screens.review.ReviewViewModel.ALL_DECKS)) },
                onOpenRoute = open
            )
        }
        composable(Routes.COURSE) {
            CourseScreen(currentRoute, navigateTopLevel, onOpenLesson = { open(Routes.lesson(it)) })
        }
        composable(Routes.PRACTICE) {
            PracticeScreen(currentRoute, navigateTopLevel, onOpen = open)
        }
        composable(Routes.DECKS) {
            DeckListScreen(
                currentRoute = currentRoute,
                onNavigateTopLevel = navigateTopLevel,
                onOpenDeck = { open(Routes.deckDetail(it)) },
                onStudyDeck = { open(Routes.review(it)) },
                onStudyCourse = { open(Routes.reviewCourse(it)) }
            )
        }
        composable(Routes.STATS) {
            StatsScreen(currentRoute = currentRoute, onNavigateTopLevel = navigateTopLevel)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(currentRoute = currentRoute, onNavigateTopLevel = navigateTopLevel, onPlacement = { open(Routes.PLACEMENT) })
        }
        composable(Routes.PLACEMENT) {
            PlacementScreen(onDone = back)
        }
        composable(Routes.LESSON, arguments = listOf(navArgument("unitId") { type = NavType.StringType })) {
            LessonScreen(
                onBack = back,
                onOpenLesson = { id ->
                    navController.navigate(Routes.lesson(id)) {
                        popUpTo(Routes.LESSON) { inclusive = true }
                    }
                },
                onOpenSettings = openSettings
            )
        }
        composable(Routes.DRILL, arguments = listOf(navArgument("mode") { type = NavType.StringType })) {
            DrillScreen(onBack = back)
        }
        composable(Routes.WRITING) { WritingScreen(onBack = back, onOpenSettings = openSettings) }
        composable(Routes.DICTIONARY) { DictionaryScreen(onBack = back) }
        composable(Routes.TUTOR_PICK) {
            ScenarioPickerScreen(onBack = back, onOpenScenario = { open(Routes.tutor(it)) }, onOpenSettings = openSettings)
        }
        composable(Routes.TUTOR, arguments = listOf(navArgument("scenario") { type = NavType.StringType })) {
            TutorScreen(onBack = back, onOpenSettings = openSettings)
        }
        composable(
            Routes.DECK_DETAIL,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) {
            val deckId = it.arguments?.getLong("deckId") ?: return@composable
            DeckDetailScreen(
                onBack = back,
                onStudy = { open(Routes.review(deckId)) },
                onAddCard = { open(Routes.cardEditor(deckId)) },
                onEditCard = { cardId -> open(Routes.cardEditor(deckId, cardId)) },
                onDeleted = { navController.popBackStack(Routes.DECKS, inclusive = false) }
            )
        }
        composable(
            Routes.CARD_EDITOR,
            arguments = listOf(
                navArgument("deckId") { type = NavType.LongType },
                navArgument("cardId") { type = NavType.LongType; defaultValue = -1L }
            )
        ) {
            CardEditorScreen(onBack = back)
        }
        composable(
            Routes.REVIEW,
            arguments = listOf(
                navArgument("deckId") { type = NavType.LongType },
                navArgument("course") { type = NavType.StringType; defaultValue = "" }
            )
        ) {
            ReviewScreen(onBack = back)
        }
    }
}
