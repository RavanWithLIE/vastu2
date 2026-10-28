package com.mycompany.myapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

data class CalendarUiState(
    val response: MonthEventsResponse?=null,
    val isLoading: Boolean=false,
    val error: String?=null
)

@HiltViewModel
class CalendarViewModel @Inject constructor(private val repository: CalendarRepository): ViewModel(){

    private val _uiState=MutableStateFlow(CalendarUiState())
    val uiState=_uiState.asStateFlow()

    fun loadMonth(month: YearMonth){
        viewModelScope.launch {
            _uiState.value= CalendarUiState(
                response = _uiState.value.response,
                isLoading = true
            )

            try{
                val result=repository.getEvents(
                    year = month.year,
                    month=month.monthValue
                )

                _uiState.value= CalendarUiState(
                    response = result,
                )
            } catch (e: Exception){
                _uiState.value= CalendarUiState(
                    response = _uiState.value.response,
                    error=e.message?: "Unable to load Events"
                )
            }
        }
    }
}