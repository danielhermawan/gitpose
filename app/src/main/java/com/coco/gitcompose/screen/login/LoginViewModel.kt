package com.coco.gitcompose.screen.login

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coco.gitcompose.R
import com.coco.gitcompose.core.common.getMutableStateFlow
import com.coco.gitcompose.usecase.GithubAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val githubAuthUseCase: GithubAuthUseCase
): ViewModel() {
    private var githubState: String? = null

    private val _uiState = savedStateHandle.getMutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun loginGithub() {
        githubState = UUID.randomUUID().toString()
        val githubOauthUrl = githubAuthUseCase.getGithubOauthUrl(githubState!!)

        _uiState.update {
            it.copy(
                githubAuthUrl = githubOauthUrl
            )
        }
    }

    fun onUserMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    fun getAccessToken(code: String, state: String) {
        if (state != githubState) {
            _uiState.update {
                it.copy(userMessage = R.string.login_github_error)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            try {
                githubAuthUseCase.generateAndSaveAccessToken(code)
                _uiState.update {
                    it.copy(isLoading = false, loginSuccess = true)
                }
            } catch (e: Exception) {
                Log.e(LoginViewModel::class.java.name, "Error at generate token", e)
                _uiState.update {
                    it.copy(userMessage = R.string.login_github_error, isLoading = false)
                }

            }
        }
    }
}