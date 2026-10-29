package com.denil.profe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.prefs.UserPreferences
import com.denil.profe.data.prefs.UserSettings
import com.denil.profe.tts.Speaker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    prefs: UserPreferences,
    speaker: Speaker
) : ViewModel() {

    val settings: StateFlow<UserSettings> = prefs.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettings())

    init {
        viewModelScope.launch { speaker.baseRate = prefs.current().speechRate }
    }
}
