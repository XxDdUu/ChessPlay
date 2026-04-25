package com.sky.chessplay.ui.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sky.chessplay.ui.component.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController,
    title: String = "",
    showFab: Boolean = false,
    showBottomBar: Boolean = false,
    onFabClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(

        topBar = {
            if (title.isNotEmpty()) {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (currentRoute != "home") {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                            }
                        }
                    }
                )
            }
        },

        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = onFabClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        },

        floatingActionButtonPosition = FabPosition.Center,

        bottomBar = {
            if (showBottomBar) {
                BottomBar()
            }
        }

    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content(padding)
        }
    }
}
