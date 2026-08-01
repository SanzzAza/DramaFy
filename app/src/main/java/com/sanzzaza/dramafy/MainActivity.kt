package com.sanzzaza.dramafy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sanzzaza.dramafy.ui.navigation.Routes
import com.sanzzaza.dramafy.ui.screen.bookmark.BookmarkScreen
import com.sanzzaza.dramafy.ui.screen.detail.DetailScreen
import com.sanzzaza.dramafy.ui.screen.home.HomeScreen
import com.sanzzaza.dramafy.ui.screen.language.LanguageScreen
import com.sanzzaza.dramafy.ui.screen.player.PlayerScreen
import com.sanzzaza.dramafy.ui.screen.search.SearchScreen
import com.sanzzaza.dramafy.ui.theme.DramaFyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val appVm: AppViewModel = hiltViewModel()
            val darkMode by appVm.darkMode.collectAsStateWithLifecycle()
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val useDark = when (darkMode) {
                "light" -> false
                "dark" -> true
                else -> systemDark
            }
            DramaFyTheme(darkTheme = useDark) {
                AppRoot()
            }
        }
    }
}

@Composable
private fun AppRoot() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val showBottomBar = currentRoute in setOf(Routes.HOME, Routes.BOOKMARKS)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    currentRoute = currentRoute,
                    onHome = { navController.navigate(Routes.HOME) { launchSingleTop = true; popUpTo(Routes.HOME) { inclusive = false } } },
                    onSearch = { navController.navigate(Routes.SEARCH) { launchSingleTop = true } },
                    onBookmarks = { navController.navigate(Routes.BOOKMARKS) { launchSingleTop = true } }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onBookClick = { id -> navController.navigate(Routes.detail(id)) },
                    onSearchClick = { navController.navigate(Routes.SEARCH) },
                    onLanguageClick = { navController.navigate(Routes.LANGUAGE) }
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onBookClick = { id -> navController.navigate(Routes.detail(id)) }
                )
            }
            composable(Routes.BOOKMARKS) {
                BookmarkScreen(
                    onBack = { navController.popBackStack() },
                    onBookClick = { id -> navController.navigate(Routes.detail(id)) }
                )
            }
            composable(Routes.LANGUAGE) {
                LanguageScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) {
                DetailScreen(
                    onBack = { navController.popBackStack() },
                    onPlay = { bookId, idx -> navController.navigate(Routes.player(bookId, idx)) }
                )
            }
            composable(
                route = Routes.PLAYER,
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType },
                    navArgument("episodeIndex") { type = NavType.IntType }
                )
            ) {
                PlayerScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun BottomBar(
    currentRoute: String?,
    onHome: () -> Unit,
    onSearch: () -> Unit,
    onBookmarks: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            BarItem(
                icon = Icons.Filled.Home,
                label = "Home",
                selected = currentRoute == Routes.HOME,
                onClick = onHome
            )
            BarItem(
                icon = Icons.Filled.Search,
                label = "Search",
                selected = false,
                onClick = onSearch
            )
            BarItem(
                icon = Icons.Filled.Bookmark,
                label = "Saved",
                selected = currentRoute == Routes.BOOKMARKS,
                onClick = onBookmarks
            )
        }
    }
}

@Composable
private fun BarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else androidx.compose.ui.graphics.Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onClick) {
                Icon(imageVector = icon, contentDescription = label, tint = tint)
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal),
            color = tint
        )
    }
}
