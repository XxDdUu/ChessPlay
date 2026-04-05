package com.sky.chessplay.data.remote

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class GoogleAuthClient(
    private val context: Context
) {

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("231704732411-28a2q7t0ftbnm47tfba6k5aut9lq69n0.apps.googleusercontent.com")
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(
        intent: Intent?,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)

        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken != null) {
                onSuccess(idToken)
            } else {
                onError(Exception("ID Token is null"))
            }

        } catch (e: Exception) {
            onError(e)
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
    }
}
