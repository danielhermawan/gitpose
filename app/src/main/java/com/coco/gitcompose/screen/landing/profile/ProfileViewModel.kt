package com.coco.gitcompose.screen.landing.profile

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.MessageType
import com.coco.gitcompose.core.ui.SnackbarState
import com.coco.gitcompose.core.ui.theme.Pink40
import com.coco.gitcompose.datamodel.CurrentUser
import com.coco.gitcompose.datamodel.RepoDataModel
import com.coco.gitcompose.datamodel.RepoSort
import com.coco.gitcompose.screen.landing.LandingViewModel
import com.coco.gitcompose.usecase.DefaultGithubUserUseCase
import com.coco.gitcompose.usecase.GithubAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val githubUserUseCase: DefaultGithubUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onTabSelected() {
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

            try {
                _uiState.update {
                    it.copy(
                        recentRepos = RecentRepoUiState.Loading
                    )
                }
                val recentRepos = githubUserUseCase.getRemoteCurrentUserRepository(
                    sort = RepoSort.pushed, perPage = 10
                )
                updateRecentRepo(recentRepos)
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

    private fun updateRecentRepo(dataModels: List<RepoDataModel>) {
        _uiState.update {
            it.copy(
                recentRepos = RecentRepoUiState.Success(
                    dataModels.map { dataModel ->
                        RecentRepoViewModel(
                            id = dataModel.id,
                            link = "${dataModel.owner}/${dataModel.name}",
                            profilePictureUrl = dataModel.owner.avatarUrl,
                            ownerName = dataModel.owner.login,
                            name = dataModel.name,
                            description = dataModel.description,
                            starCount = dataModel.stargazersCount,
                            language = dataModel.language,
                            color = Pink40
                        )
                    }.toImmutableList()
                )
            )
        }
    }

}