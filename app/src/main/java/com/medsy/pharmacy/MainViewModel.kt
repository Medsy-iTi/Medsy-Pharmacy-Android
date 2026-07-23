package com.medsy.pharmacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import android.content.Context
import com.medsy.pharmacy.presence.PresenceForegroundService

data class MainState(
    val themeMode: ThemeMode? = null,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observePreferences: ObserveUserPreferencesUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val state = observePreferences()
        .map { MainState(themeMode = it.themeMode) }
        .catch { emit(MainState(themeMode = ThemeMode.System)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MainState(),
        )

    init {
        observePresence()
    }

    private fun observePresence() {
        viewModelScope.launch {
            observePreferences().collect { prefs ->
                if (prefs.isReceivingOrders) {
                    PresenceForegroundService.start(context)
                } else {
                    PresenceForegroundService.stop(context)
                }
            }
        }
    }
}
