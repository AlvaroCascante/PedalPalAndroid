package com.quetoquenana.and.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.home.domain.usecase.GetHomeContentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeContentUseCase: GetHomeContentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    headerSection = HeaderSection.Loading
                )
            }
            try {
                val result = getHomeContentUseCase()
                val headerSection = if (result.bikes.isEmpty()) {
                    HeaderSection.NoBikes()
                } else {
                    HeaderSection.Content(
                        appointments = result.appointments,
                    )
                }
                _uiState.update { it.copy(
                    headerSection = headerSection,
                    bikes = result.bikes,
                    suggestions = result.suggestions,
                    announcements = result.announcements,
                    isLoading = false
                )}
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Failed to load home content")
                _uiState.update {
                    it.copy(
                        headerSection = HeaderSection.NoBikes(),
                        isLoading = false
                    )
                }
            }
        }
    }
}