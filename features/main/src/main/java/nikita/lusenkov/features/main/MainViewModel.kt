package nikita.lusenkov.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nikita.lusenkov.domain.weather.Forecast
import nikita.lusenkov.domain.weather.GetMoscowForecastUseCase

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMoscowForecast: GetMoscowForecastUseCase
) : ViewModel() {

    sealed interface UiState {
        object Loading : UiState
        data class Success(val data: Forecast) : UiState
        data class Error(val message: String) : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            getMoscowForecast().collect { result ->
                if (result.isSuccess) {
                    _state.value = UiState.Success(result.getOrThrow())
                } else {
                    _state.value = UiState.Error(
                        result.exceptionOrNull()?.message ?: "Не удалось загрузить данные"
                    )
                }
            }
        }
    }
}