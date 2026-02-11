package com.quetoquenana.and.core.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class MainViewModel @Inject constructor(
    // TODO: inject GetPendingAppointmentsCountUseCase when available
) : ViewModel() {

    private val _appointmentsBadgeCount = MutableStateFlow(0)
    val appointmentsBadgeCount: StateFlow<Int> = _appointmentsBadgeCount


}
