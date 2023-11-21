package com.coco.gitcompose.screen.landing.profile

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coco.gitcompose.R
import com.coco.gitcompose.core.common.getMutableStateFlow
import com.coco.gitcompose.core.datamodel.RepoSort
import com.coco.gitcompose.core.ui.MessageType
import com.coco.gitcompose.core.ui.SnackbarState
import com.coco.gitcompose.core.util.languageToColor
import com.coco.gitcompose.datamodel.CurrentUser
import com.coco.gitcompose.screen.landing.LandingViewModel
import com.coco.gitcompose.usecase.GithubAuthUseCase
import com.coco.gitcompose.usecase.GithubRepositoryUseCase
import com.coco.gitcompose.usecase.GithubUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val githubAuthUseCase: GithubAuthUseCase,
    private val githubUserUseCase: GithubUserUseCase,
    private val githubRepositoryUseCase: GithubRepositoryUseCase
) : ViewModel() {
    private val _uiState = savedStateHandle.getMutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var loadedFirstTime: Boolean = false

    fun onTabSelected() {
        if (!loadedFirstTime) {
            loadedFirstTime = true
            viewModelScope.launch {
                githubUserUseCase.getStreamCurrentUser(true)
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
                    .onEach { currentUser ->
                        updateCurrentUserData(currentUser)
                    }
                    .launchIn(this)
                loadRecentRepo()
            }
        }
    }

    fun onSnackbarMessageShown() {
        _uiState.update {
            it.copy(snackbarState = null)
        }
    }

    fun refresh() {
        if (_uiState.value.recentRepos is RecentRepoUiState.Loading) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRefreshing = true
                )
            }

            val currentUserJob = launch { githubUserUseCase.refreshCurrentUser() }
            val recentRepoJob = launch { loadRecentRepo(false) }

            try {
                currentUserJob.join()
            } catch (ex: Exception) {
                _uiState.update {
                    it.copy(
                        snackbarState = SnackbarState(
                            R.string.landing_error_load_user_data,
                            MessageType.ERROR
                        )
                    )
                }
            }
            recentRepoJob.join()

            _uiState.update {
                it.copy(
                    isRefreshing = false
                )
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
                profilePictureUrl = currentUser.avatarUrl,
                name = currentUser.name,
                username = currentUser.login,
                followers = currentUser.followers,
                totalRepo = currentUser.totalPrivateRepos + currentUser.publicRepos
            )
        }
    }

    private suspend fun loadRecentRepo(needLoading: Boolean = true) {
        try {
            if (needLoading || _uiState.value.recentRepos is RecentRepoUiState.Error) {
                _uiState.update {
                    it.copy(
                        recentRepos = RecentRepoUiState.Loading
                    )
                }
            }

            val recentRepos = githubRepositoryUseCase.getRemoteCurrentUserRepository(
                sort = RepoSort.PUSHED, perPage = 10
            )
            _uiState.update {
                it.copy(
                    recentRepos = RecentRepoUiState.Success(
                        recentRepos.map { dataModel ->
                            RecentRepoViewModel(
                                id = dataModel.id,
                                link = "${dataModel.owner}/${dataModel.name}",
                                profilePictureUrl = dataModel.owner.avatarUrl,
                                ownerName = dataModel.owner.login,
                                name = dataModel.name,
                                description = dataModel.description,
                                starCount = dataModel.stargazersCount,
                                language = dataModel.language,
                                color = dataModel.language?.languageToColor()
                            )
                        }.toImmutableList()
                    )
                )
            }
        } catch (ex: Exception) {
            Log.e(LandingViewModel::class.simpleName, ex.message, ex)
            _uiState.update {
                it.copy(
                    snackbarState = SnackbarState(
                        R.string.landing_error_load_repo_data,
                        MessageType.ERROR
                    ),
                    recentRepos = RecentRepoUiState.Error()
                )
            }
        }
    }

}