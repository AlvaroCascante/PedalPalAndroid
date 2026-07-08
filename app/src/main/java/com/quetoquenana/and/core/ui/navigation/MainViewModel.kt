package com.quetoquenana.and.core.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.authentication.domain.usecase.GetUserDisplayNameUseCase
import com.quetoquenana.and.features.appointments.domain.usecase.ObserveUpcomingAppointmentsCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserDisplayNameUseCase: GetUserDisplayNameUseCase,
    private val observeUpcomingAppointmentsCountUseCase: ObserveUpcomingAppointmentsCountUseCase
) : ViewModel() {

    private val _appointmentsBadgeCount = MutableStateFlow(value = 0)
    val appointmentsBadgeCount: StateFlow<Int> = _appointmentsBadgeCount.asStateFlow()

    private val _userDisplayName = MutableStateFlow<String?>(value = null)
    val userDisplayName: StateFlow<String?> = _userDisplayName.asStateFlow()

    init {
        observeAppointmentsBadgeCount()
        loadUserDisplayName()
    }

    private fun observeAppointmentsBadgeCount() {
        viewModelScope.launch {
            observeUpcomingAppointmentsCountUseCase()
                .catch { _appointmentsBadgeCount.value = 0 }
                .collect { count ->
                    _appointmentsBadgeCount.value = count
                }
        }
    }

    fun loadUserDisplayName() {
        viewModelScope.launch {
            _userDisplayName.value = getUserDisplayNameUseCase()
        }
    }
}
