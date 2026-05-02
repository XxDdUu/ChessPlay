package com.sky.chessplay.ui.presentation.auth

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.sky.chessplay.BuildConfig
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.navigation.Route

@Composable
fun AuthScreenRoute(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.authState.collectAsState()
    val email by viewModel.email.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state is AuthState.Authenticated) {
            navController.navigate(Route.OnlineGameMode.route) {
                popUpTo(Route.Auth.route) { inclusive = true }
            }
        }
    }
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .build()
    }

    val googleClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            Log.d("GOOGLE", "idToken = $idToken")

            if (idToken != null) {
                viewModel.onGoogleClick(idToken)
            } else {
                Log.e("GOOGLE", "Token NULL 💀")
            }

        } catch (e: ApiException) {
            Log.e("GOOGLE", "Login failed code=${e.statusCode}", e)
        }
    }


    AuthScreen(
        email = email,
        onEmailChange = { viewModel.onEmailChange(it) },
        onNextClick = { viewModel.onNextClick() },

        onGoogleClick = {
            launcher.launch(googleClient.signInIntent)
        },

        state = state
    )
}

