package com.quetoquenana.and.core.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.authentication.domain.usecase.GetCurrentUserDisplayNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    // TODO: inject GetPendingAppointmentsCountUseCase when available
    private val getCurrentUserDisplayNameUseCase: GetCurrentUserDisplayNameUseCase
) : ViewModel() {

    private val _appointmentsBadgeCount = MutableStateFlow(0)
    val appointmentsBadgeCount: StateFlow<Int> = _appointmentsBadgeCount.asStateFlow()

    private val _userDisplayName = MutableStateFlow<String?>(null)
    val userDisplayName: StateFlow<String?> = _userDisplayName.asStateFlow()

    init {
        loadUserDisplayName()
    }

    fun loadUserDisplayName() {
        viewModelScope.launch {
            _userDisplayName.value = getCurrentUserDisplayNameUseCase()
        }
    }
}
