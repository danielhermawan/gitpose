package com.coco.gitcompose.screen.landing

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coco.gitcompose.usecase.GithubAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LandingUiState(
    val isLogin: Boolean = true,
    val logoutSuccess: Boolean = false
)

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val githubAuthUsecase: GithubAuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LandingUiState())
    val uiState: StateFlow<LandingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val isLogin = githubAuthUsecase
                .isLogin()
                .catch { emit(false) }
                .first()
            if (isLogin) {
                try {
                    val currentUserResponse = githubAuthUsecase.getCurrentUser()
                    Log.i(LandingViewModel::class.java.name, currentUserResponse.toString())
                } catch (ex: Exception) {
                    Log.e(LandingViewModel::class.java.name, ex.message, ex)
                }
            }
            _uiState.update {
                it.copy(isLogin = isLogin)
            }
        }
    }


    fun logout() {
        viewModelScope.launch {
            githubAuthUsecase.logout()
            _uiState.update {
                it.copy(logoutSuccess = true)
            }
        }
    }

}