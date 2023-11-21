package com.coco.gitcompose.screen.landing.profile

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import com.coco.gitcompose.core.ui.SnackbarState
import com.coco.gitcompose.core.util.RgbColor
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
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
    val isRefreshing: Boolean = false,
    val recentRepos: RecentRepoUiState = RecentRepoUiState.Success()
) : Parcelable

@Parcelize
data class RecentRepoViewModel(
    val id: String,
    val link: String,
    val profilePictureUrl: String,
    val ownerName: String,
    val name: String,
    val description: String?,
    val starCount: Int,
    val language: String?,
    val color: RgbColor?
) : Parcelable

@Parcelize
sealed interface RecentRepoUiState : Parcelable {
    @Parcelize
    data object Loading : RecentRepoUiState

    @Parcelize
    data class Error(
        @StringRes val messageError: Int? = null
    ) : RecentRepoUiState

    @Parcelize
    data class Success(
        val recentRepos: List<RecentRepoViewModel> = emptyList<RecentRepoViewModel>().toImmutableList()
    ) : RecentRepoUiState
}