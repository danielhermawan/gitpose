package com.coco.gitcompose.screen.landing.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.MessageType
import com.coco.gitcompose.core.ui.SnackbarState
import com.coco.gitcompose.datamodel.CurrentUser
import com.coco.gitcompose.screen.landing.LandingUiState
import com.coco.gitcompose.screen.landing.LandingViewModel
import com.coco.gitcompose.usecase.DefaultGithubUserUserCase
import com.coco.gitcompose.usecase.GithubAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val githubAuthUseCase: GithubAuthUseCase,
    private val githubUserUserCase: DefaultGithubUserUserCase
) : ViewModel(){
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onTabSelected() {
        viewModelScope.launch {
            githubUserUserCase.getStreamCurrentUser(true)
                .catch { ex ->
                    Log.e(LandingViewModel::class.simpleName, ex.message, ex)
                    _uiState.update {
                        it.copy(
                            snackbarState = SnackbarState(
                                R.string.landing_error_load_user_data,
                                MessageType.ERROR
                            )
                        )
                    }
                }
                .collect { currentUser ->
                    updateCurrentUserData(currentUser)
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            githubAuthUseCase.logout()
            _uiState.update {
                it.copy(logoutSuccess = true)
            }
        }
    }

    private fun updateCurrentUserData(currentUser: CurrentUser) {
        _uiState.update {
            it.copy(
                name = currentUser.name,
                username = currentUser.login,
                followers = currentUser.followers,
                totalRepo = currentUser.totalPrivateRepos + currentUser.publicRepos
            )
        }
    }

}