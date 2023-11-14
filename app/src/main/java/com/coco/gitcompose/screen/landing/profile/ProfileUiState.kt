package com.coco.gitcompose.screen.landing.profile

import androidx.annotation.StringRes
import com.coco.gitcompose.core.ui.SnackbarState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class ProfileUiState(
    val snackbarState: SnackbarState? = null,
    val logoutSuccess: Boolean = false,
    val name: String = "",
    val profilePictureUrl: String = "",
    val username: String = "",
    val followers: Int = 0,
    val totalRepo: Int = 0,
    val totalOrganizations: Int = 0,
    val totalStar: Int = 0,
    val recentRepos: RecentRepoUiState = RecentRepoUiState.Success()
)

data class RecentRepoViewModel(
    val profilePictureUrl: String,
    val ownerName: String,
    val name: String,
    val description: String,
    val starCount: Int,
    val language: String?
)

sealed interface RecentRepoUiState {
    data object Loading : RecentRepoUiState

    data class Error(
        @StringRes val messageError: Int? = null
    ) : RecentRepoUiState

    data class Success(
        val recentRepos: ImmutableList<RecentRepoViewModel> = emptyList<RecentRepoViewModel>().toImmutableList()
    ) : RecentRepoUiState

}