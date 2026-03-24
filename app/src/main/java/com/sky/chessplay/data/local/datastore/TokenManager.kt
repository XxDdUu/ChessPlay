package com.sky.chessplay.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun saveToken(token: String?) {
        dataStore.edit {
            it[TOKEN_KEY] = token as String
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.first()[TOKEN_KEY]
    }

    fun getTokenSync(): String? {
        return runBlocking {
            getToken()
        }
    }

    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
    }
}
