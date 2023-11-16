package com.coco.gitcompose.screen.userRepository

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.coco.gitcompose.core.common.getMutableStateFlow
import com.coco.gitcompose.usecase.GithubRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UserRepositoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val githubRepositoryUseCase: GithubRepositoryUseCase
) : ViewModel() {
    private val _uiState = savedStateHandle.getMutableStateFlow(UserRepositoryUiState())
    val uiState: StateFlow<UserRepositoryUiState> = _uiState.asStateFlow()

    fun onSnackbarMessageShown() {
        _uiState.update {
            it.copy(snackbarState = null)
        }
    }
}