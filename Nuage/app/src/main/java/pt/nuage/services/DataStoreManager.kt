package pt.nuage.services

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.doublePreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


object Settings {
    private val Context.dataStore by preferencesDataStore(name = "user_settings")

    val LATITUDE = doublePreferencesKey("latitude")
    val LONGITUDE = doublePreferencesKey("longitude")
    private const val DEFAULT_LATITUDE = 40.1403
    private const val DEFAULT_LONGITUDE = -7.5014


    suspend fun saveLocation(context: Context, latitude: Double, longitude: Double) {
        context.dataStore.edit { prefs ->
            prefs[LATITUDE] = latitude
            prefs[LONGITUDE] = longitude
        }
    }

    fun getLatitudeFlow(context: Context): Flow<Double?> =
        context.dataStore.data.map { it[LATITUDE] ?: DEFAULT_LATITUDE }

    fun getLongitudeFlow(context: Context): Flow<Double?> =
        context.dataStore.data.map { it[LONGITUDE] ?: DEFAULT_LONGITUDE }
}
