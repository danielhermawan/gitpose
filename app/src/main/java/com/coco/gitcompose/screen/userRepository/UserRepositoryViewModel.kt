package com.coco.gitcompose.screen.userRepository

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coco.gitcompose.R
import com.coco.gitcompose.core.common.getMutableStateFlow
import com.coco.gitcompose.core.datamodel.RepoDataModel
import com.coco.gitcompose.core.datamodel.RepoSort
import com.coco.gitcompose.core.datamodel.RepoType
import com.coco.gitcompose.core.datamodel.SortBy
import com.coco.gitcompose.core.ui.MessageType
import com.coco.gitcompose.core.ui.SnackbarState
import com.coco.gitcompose.core.util.languageToColor
import com.coco.gitcompose.usecase.GithubRepositoryUseCase
import com.coco.gitcompose.usecase.GithubUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserRepositoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val githubUserUseCase: GithubUserUseCase,
    private val githubRepositoryUseCase: GithubRepositoryUseCase
) : ViewModel() {
    private val _uiState = savedStateHandle.getMutableStateFlow(UserRepositoryUiState())
    val uiState: StateFlow<UserRepositoryUiState> = _uiState.asStateFlow()

    private var loadJobRepo: Job? = null

    init {
        viewModelScope.launch {
            githubUserUseCase
                .getStreamCurrentUser(true)
                .catch {
                    _uiState.update {
                        it.copy(
                            snackbarState = SnackbarState(
                                R.string.common_server_error,
                                MessageType.ERROR
                            )
                        )
                    }
                }
                .onEach { user ->
                    _uiState.update {
                        it.copy(
                            loginName = user.login
                        )
                    }
                }
                .launchIn(this)
        }

        loadRepo(
            selectedSortBy = SortBy.DESC,
            selectedRepoSort = RepoSort.PUSHED,
            selectedType = RepoType.ALL
        )
    }

    fun filterTypeSelected(repoTypeLabel: RepoTypeLabel) {
        loadRepo(
            showFullLoading = true,
            selectedType = repoTypeLabel.repoType
        )
    }

    fun filterSortSelected(sortLabel: SortLabel) {
        loadRepo(
            showFullLoading = true,
            selectedSortBy = sortLabel.sortBy,
            selectedRepoSort = sortLabel.repoSort
        )
    }

    fun resetFilter() {
        loadRepo(
            showFullLoading = true,
            selectedSortBy = SortBy.DESC,
            selectedRepoSort = RepoSort.PUSHED,
            selectedType = RepoType.ALL
        )
    }

    fun reloadPage() {
        loadRepo(
            showFullLoading = true,
            showPullToRefresh = false
        )
    }

    fun onPullToRefresh() {
        loadRepo(
            showFullLoading = false,
            showPullToRefresh = true
        )
    }

    fun loadNextPage() {
        loadRepo(showFullLoading = false, showPullToRefresh = false)
    }

    private fun loadRepo(
        showFullLoading: Boolean = true,
        showPullToRefresh: Boolean = false,
        loadNextPage: Boolean = false,
        selectedSortBy: SortBy = _uiState.value.selectedSortOption.sortBy,
        selectedRepoSort: RepoSort = _uiState.value.selectedSortOption.repoSort,
        selectedType: RepoType = _uiState.value.selectedRepoType.repoType
    ) {
        updateSortAndFilter(selectedSortBy, selectedRepoSort, selectedType)

        var page = _uiState.value.currentPage
        if (!loadNextPage) {
            page = 0
            loadJobRepo?.cancel()
        }
        loadJobRepo = viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isPullToRefresh = showPullToRefresh,
                    ownerRepoUiState = if (showFullLoading) OwnerRepoUiState.Loading else state.ownerRepoUiState
                )
            }
            try {
                val savedInCache = selectedType == RepoType.ALL
                val ownerRepos =
                    githubRepositoryUseCase.getRemoteCurrentUserRepository(
                        sort = selectedRepoSort,
                        perPage = 10,
                        page = page + 1,
                        sortBy = selectedSortBy,
                        filterBy = selectedType,
                        savedInCache = savedInCache,
                        replaceCache = true
                    ).map { mapRepoDataModel(it) }

                _uiState.update { state ->
                    state.copy(
                        isPullToRefresh = false,
                        currentPage = page + 1,
                        loadingNextPage = ownerRepos.isNotEmpty(),
                        ownerRepoUiState = if (ownerRepos.isEmpty()) OwnerRepoUiState.Empty(
                            !state.selectedRepoType.default || !state.selectedSortOption.default
                        ) else OwnerRepoUiState.Success(ownerRepos)
                    )
                }
            } catch (ex: Exception) {
                if (ex !is CancellationException) {
                    Log.e(UserRepositoryViewModel::class.simpleName, ex.message, ex)
                    _uiState.update { state ->
                        if (showFullLoading) {
                            state.copy(
                                isPullToRefresh = false,
                                loadingNextPage = false,
                                ownerRepoUiState = OwnerRepoUiState.Error(messageError = R.string.common_server_error)
                            )
                        } else {
                            state.copy(
                                isPullToRefresh = false,
                                loadingNextPage = false,
                                snackbarState = SnackbarState(
                                    R.string.common_server_error,
                                    MessageType.ERROR
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSnackbarMessageShown() {
        _uiState.update {
            it.copy(snackbarState = null)
        }
    }

    private fun updateSortAndFilter(
        selectedSortBy: SortBy, selectedRepoSort: RepoSort, selectedType: RepoType
    ) {
        val sortLabels = listOf(
            SortLabel(
                RepoSort.PUSHED,
                SortBy.DESC,
                R.string.user_repo_recently_pushed,
                selected = selectedRepoSort == RepoSort.PUSHED && selectedSortBy == SortBy.DESC,
                default = true
            ),
            SortLabel(
                RepoSort.PUSHED,
                SortBy.ASC,
                R.string.user_repo_least_recently_pushed,
                true,
                selected = selectedRepoSort == RepoSort.PUSHED && selectedSortBy == SortBy.ASC
            ),

            SortLabel(
                RepoSort.CREATED,
                SortBy.DESC,
                R.string.user_repo_newest_update,
                selected = selectedRepoSort == RepoSort.CREATED && selectedSortBy == SortBy.DESC
            ),
            SortLabel(
                RepoSort.CREATED,
                SortBy.ASC,
                R.string.user_repo_oldest_update,
                true,
                selected = selectedRepoSort == RepoSort.CREATED && selectedSortBy == SortBy.ASC
            ),

            SortLabel(
                RepoSort.UPDATED,
                SortBy.DESC,
                R.string.user_repo_newest_created,
                selected = selectedRepoSort == RepoSort.UPDATED && selectedSortBy == SortBy.DESC
            ),
            SortLabel(
                RepoSort.UPDATED,
                SortBy.ASC,
                R.string.user_repo_oldest_created,
                true,
                selected = selectedRepoSort == RepoSort.UPDATED && selectedSortBy == SortBy.ASC
            ),

            SortLabel(
                RepoSort.FULL_NAME,
                SortBy.ASC,
                R.string.user_repo_name_asc,
                selected = selectedRepoSort == RepoSort.FULL_NAME && selectedSortBy == SortBy.ASC
            ),
            SortLabel(
                RepoSort.FULL_NAME,
                SortBy.DESC,
                R.string.user_repo_name_desc,
                selected = selectedRepoSort == RepoSort.FULL_NAME && selectedSortBy == SortBy.DESC
            )
        )

        val filterLabels = listOf(
            RepoTypeLabel(
                RepoType.ALL,
                R.string.user_repo_type_all,
                selected = selectedType == RepoType.ALL,
                default = true
            ),
            RepoTypeLabel(
                RepoType.PRIVATE,
                R.string.user_repo_type_private,
                selected = selectedType == RepoType.PRIVATE
            ),
            RepoTypeLabel(
                RepoType.PUBLIC,
                R.string.user_repo_type_public,
                selected = selectedType == RepoType.PUBLIC
            ),
            RepoTypeLabel(
                RepoType.OWNER,
                R.string.user_repo_type_owner,
                selected = selectedType == RepoType.OWNER
            ),
            RepoTypeLabel(
                RepoType.MEMBER,
                R.string.user_repo_type_member,
                selected = selectedType == RepoType.MEMBER
            ),
        )

        var filterCount = 0
        val selectedSort = sortLabels.first {
            it.selected
        }
        if (!selectedSort.default) {
            filterCount += 1
        }

        val selectedFilter = filterLabels.first {
            it.selected
        }
        if (!selectedFilter.default) {
            filterCount += 1
        }

        _uiState.update {
            it.copy(
                listSortOptions = sortLabels,
                listRepoTypes = filterLabels,
                selectedSortOption = selectedSort,
                selectedRepoType = selectedFilter,
                filterCount = filterCount
            )
        }
    }

    private fun mapRepoDataModel(repo: RepoDataModel): OwnerRepoViewModel {
        return OwnerRepoViewModel(
            id = repo.id,
            link = "${repo.owner}/${repo.name}",
            forkedFrom = repo.parent?.fullName,
            name = repo.name,
            description = repo.description,
            starCount = repo.stargazersCount,
            language = repo.language,
            color = repo.language?.languageToColor()
        )
    }
}