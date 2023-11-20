package com.coco.gitcompose.screen.userRepository

import android.os.Parcelable
import androidx.annotation.StringRes
import com.coco.gitcompose.core.datamodel.RepoSort
import com.coco.gitcompose.core.datamodel.RepoType
import com.coco.gitcompose.core.datamodel.SortBy
import com.coco.gitcompose.core.ui.SnackbarState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserRepositoryUiState(
    val snackbarState: SnackbarState? = null,
    val selectedSortOption: SortLabel = SortLabel(RepoSort.PUSHED, SortBy.DESC, 0),
    val listSortOptions: List<SortLabel> = emptyList(),
    val selectedRepoType: RepoTypeLabel = RepoTypeLabel(RepoType.ALL, 0),
    val listRepoTypes: List<RepoTypeLabel> = emptyList(),
    val ownerRepoUiState: OwnerRepoUiState = OwnerRepoUiState.Loading,
    val currentPage: Int = 1,
    val loadingNextPage: Boolean = true,
    val isPullToRefresh: Boolean = false,
    val loginName: String = ""
) : Parcelable

@Parcelize
data class OwnerRepoViewModel(
    val id: String,
    val link: String,
    val forkedFrom: String?,
    val name: String,
    val description: String?,
    val starCount: Int,
    val language: String?,
    val color: Long
) : Parcelable

@Parcelize
data class SortLabel(
    val repoSort: RepoSort,
    val sortBy: SortBy,
    @StringRes val label: Int,
    val divider: Boolean = false,
    val selected: Boolean = false
) : Parcelable

@Parcelize
data class RepoTypeLabel(
    val repoType: RepoType,
    @StringRes val label: Int,
    val selected: Boolean = false
) : Parcelable

@Parcelize
sealed interface OwnerRepoUiState : Parcelable {
    @Parcelize
    data object Loading : OwnerRepoUiState

    @Parcelize
    data class Error(
        @StringRes val messageError: Int? = null
    ) : OwnerRepoUiState

    @Parcelize
    data class Success(
        val recentRepos: List<OwnerRepoViewModel> = emptyList<OwnerRepoViewModel>().toImmutableList()
    ) : OwnerRepoUiState
}