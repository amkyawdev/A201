package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ApiSettingsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocsScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.ExportScreen
import com.example.ui.screens.RecapGeneratorScreen
import com.example.ui.screens.SubtitlesScreen
import com.example.ui.screens.TranscriptionScreen
import com.example.ui.screens.UploadMovieScreen
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.StudioViewModel

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@Composable
fun AppNavigation(viewModel: StudioViewModel) {
    val navController = rememberNavController()

    val navItems = listOf(
        BottomNavItem(Screen.Dashboard, Icons.Default.Home, "Studio"),
        BottomNavItem(Screen.Upload, Icons.Default.CloudUpload, "Upload"),
        BottomNavItem(Screen.Generator, Icons.Default.AutoAwesome, "AI Script"),
        BottomNavItem(Screen.Editor, Icons.Default.VideoLibrary, "Editor"),
        BottomNavItem(Screen.Transcription, Icons.Default.Mic, "Audio"),
        BottomNavItem(Screen.Subtitles, Icons.Default.Subtitles, "SRT"),
        BottomNavItem(Screen.Export, Icons.Default.Download, "Export"),
        BottomNavItem(Screen.Settings, Icons.Default.Settings, "API"),
        BottomNavItem(Screen.Docs, Icons.Default.Description, "Docs")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CardCharcoal,
                contentColor = TextPrimary
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { item ->
                    val selected = currentRoute == item.screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (selected) MetallicGold else TextMuted
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                color = if (selected) MetallicGold else TextMuted
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = ElectricViolet.copy(alpha = 0.25f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .padding(innerPadding)
                .background(DarkCharcoal)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToUpload = { navController.navigate(Screen.Upload.route) },
                    onNavigateToGenerator = { navController.navigate(Screen.Generator.route) },
                    onNavigateToEditor = { navController.navigate(Screen.Editor.route) },
                    onNavigateToAudio = { navController.navigate(Screen.Transcription.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Upload.route) {
                UploadMovieScreen(
                    viewModel = viewModel,
                    onProceedToScript = { navController.navigate(Screen.Editor.route) }
                )
            }

            composable(Screen.Generator.route) {
                RecapGeneratorScreen(
                    viewModel = viewModel,
                    onScriptGenerated = { navController.navigate(Screen.Editor.route) }
                )
            }

            composable(Screen.Editor.route) {
                EditorScreen(
                    viewModel = viewModel,
                    onNavigateToExport = { navController.navigate(Screen.Export.route) },
                    onNavigateToSubtitles = { navController.navigate(Screen.Subtitles.route) }
                )
            }

            composable(Screen.Transcription.route) {
                TranscriptionScreen(
                    viewModel = viewModel,
                    onSubtitlesGenerated = { navController.navigate(Screen.Subtitles.route) }
                )
            }

            composable(Screen.Subtitles.route) {
                SubtitlesScreen(viewModel = viewModel)
            }

            composable(Screen.Export.route) {
                ExportScreen(viewModel = viewModel)
            }

            composable(Screen.Settings.route) {
                ApiSettingsScreen(viewModel = viewModel)
            }

            composable(Screen.Docs.route) {
                DocsScreen()
            }
        }
    }
}
