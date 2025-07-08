package pt.nuage.services

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


object Settings {
    private val Context.dataStore by preferencesDataStore(name = "user_settings")

    val LATITUDE = doublePreferencesKey("latitude")
    val LONGITUDE = doublePreferencesKey("longitude")
    val LOCALITY = stringPreferencesKey("Fundão")
    private const val DEFAULT_LATITUDE = 40.1403
    private const val DEFAULT_LONGITUDE = -7.5014
    private const val DEFAULT_LOCALITY = "Fundão"


    suspend fun saveLocation(context: Context, latitude: Double, longitude: Double, locality: String) {
        context.dataStore.edit { prefs ->
            prefs[LATITUDE] = latitude
            prefs[LONGITUDE] = longitude
            prefs[LOCALITY]= locality
        }
    }


    fun getLocality(context: Context): Flow<String?> = context.dataStore.data.map { it[LOCALITY] ?: DEFAULT_LOCALITY }

    fun getLatitudeFlow(context: Context): Flow<Double?> =
        context.dataStore.data.map { it[LATITUDE] ?: DEFAULT_LATITUDE }

    fun getLongitudeFlow(context: Context): Flow<Double?> =
        context.dataStore.data.map { it[LONGITUDE] ?: DEFAULT_LONGITUDE }
}
