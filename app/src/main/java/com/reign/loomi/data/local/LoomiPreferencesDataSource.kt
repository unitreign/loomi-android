package com.reign.loomi.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.reign.loomi.data.model.PersistedSnapshot
import kotlinx.coroutines.flow.first

private val Context.loomiDataStore by preferencesDataStore(name = "loomi_preferences")

class LoomiPreferencesDataSource(
    private val context: Context,
    private val gson: Gson = Gson(),
) {
    private val snapshotKey = stringPreferencesKey("snapshot_json")

    suspend fun loadSnapshot(): PersistedSnapshot {
        val prefs = context.loomiDataStore.data.first()
        val json = prefs[snapshotKey] ?: return PersistedSnapshot()
        return runCatching {
            gson.fromJson(json, PersistedSnapshot::class.java) ?: PersistedSnapshot()
        }.getOrElse {
            PersistedSnapshot()
        }
    }

    suspend fun saveSnapshot(snapshot: PersistedSnapshot) {
        context.loomiDataStore.edit { prefs ->
            prefs[snapshotKey] = gson.toJson(snapshot)
        }
    }
}
