package com.sky.chessplay.ui.layout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.component.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController,
    config: AppScaffoldConfig,
    content: @Composable (PaddingValues) -> Unit
) {

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(

        topBar = {

            if (config.showTopBar && config.title.isNotEmpty())  {

                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1C1A17),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),

                    title = {
                        Text(config.title)
                    },

                    navigationIcon = {

                        if (currentRoute != Route.Home.route) {

                            IconButton(
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {

                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    },

                    actions = {
                        config.createTournamentAction?.let { onCreate ->

                            OutlinedButton(
                                onClick = onCreate,
                                border = BorderStroke(
                                    1.dp,
                                    Color.White
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(Modifier.width(4.dp))

                                Text("Create")
                            }
                        }

                        config.actions.forEach { action ->

                            when (action) {

                                is TopBarAction.Chat -> {

                                    IconButton(
                                        onClick = action.onClick
                                    ) {

                                        BadgedBox(

                                            badge = {

                                                if (action.unreadCount > 0) {

                                                    Badge {

                                                        Text(
                                                            text =
                                                                if (action.unreadCount > 99) "99+"
                                                                else action.unreadCount.toString()
                                                        )
                                                    }
                                                }
                                            }
                                        ) {

                                            Icon(
                                                Icons.Default.Chat,
                                                contentDescription = "Chat"
                                            )
                                        }
                                    }
                                }

                                is TopBarAction.AddFriend -> {

                                    IconButton(
                                        onClick = action.onClick,
                                        enabled = !action.isRequestSent
                                    ) {

                                        Icon(
                                            imageVector = if (action.isRequestSent) {
                                                Icons.Default.Check
                                            } else {
                                                Icons.Default.PersonAdd
                                            },
                                            contentDescription = if (action.isRequestSent) {
                                                "Friend Request Sent"
                                            } else {
                                                "Add Friend"
                                            }
                                        )
                                    }
                                }


                                is TopBarAction.History -> {

                                    IconButton(onClick = action.onClick) {
                                        Icon(
                                            Icons.Default.History,
                                            contentDescription = "History"
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        },

        floatingActionButton = {

            config.fab?.let { fab ->

                FloatingActionButton(
                    onClick = fab.onClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {

                    Icon(
                        imageVector = fab.icon,
                        contentDescription = fab.contentDescription,
                        tint = Color.White
                    )
                }
            }
        },

        floatingActionButtonPosition = FabPosition.Center,

        bottomBar = {
            if (config.showBottomBar) {
                BottomBar(
                    onHomeClick = {
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Home.route) { inclusive = true }
                        }
                    },
                    onCommunityClick = {
                        navController.navigate(Route.Friend.route)
                    },
                    onProfileClick = {
                        navController.navigate(Route.Profile.route)
                    }
                )
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier.padding(padding)
        ) {

            content(PaddingValues(0.dp))
        }
    }
}
data class AppScaffoldConfig(
    val title: String = "",
    val fab: FabConfig? = null,
    val actions: List<TopBarAction> = emptyList(),
    val showBottomBar: Boolean = false,
    val showTopBar: Boolean = true,
    val createTournamentAction: (() -> Unit)? = null
)
data class FabConfig(
    val onClick: () -> Unit,
    val icon: ImageVector,
    val contentDescription: String = ""
)
sealed class TopBarAction {

    data class Chat(
        val unreadCount: Int = 0,
        val onClick: () -> Unit
    ) : TopBarAction()

    data class AddFriend(
        val isRequestSent: Boolean = false,
        val onClick: () -> Unit
    ) : TopBarAction()

    data class History(
        val onClick: () -> Unit
    ) : TopBarAction()
}