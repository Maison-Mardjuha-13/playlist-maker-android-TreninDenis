package com.example.playlistmaker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferences(context: Context) {
    val dataStore = context.dataStore
}