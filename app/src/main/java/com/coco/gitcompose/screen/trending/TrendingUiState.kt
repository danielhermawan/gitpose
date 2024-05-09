package com.coco.gitcompose.screen.trending

import androidx.annotation.StringRes
import com.coco.gitcompose.R
import com.coco.gitcompose.core.datamodel.RepoDataModel
import com.coco.gitcompose.core.ui.MessageType
import com.coco.gitcompose.core.ui.SnackbarState
import com.coco.gitcompose.core.util.getId
import kotlinx.collections.immutable.toImmutableList

enum class FilterTime {
    TODAY, THIS_WEEK, THIS_MONTH
}

fun FilterTime.getDay() = when (this) {
    FilterTime.THIS_MONTH -> 30
    FilterTime.THIS_WEEK -> 7
    else -> 1
}

data class TrendingUiState(
    val snackbarState: SnackbarState? = null,
    val filterState: FilterState = FilterState(),
    val trendingState: TrendingState = TrendingState.Success()
)

fun TrendingUiState.showError(@StringRes message: Int = R.string.common_server_error): TrendingUiState {
    val snackbarState = if (trendingState is TrendingState.Success) SnackbarState(
        message,
        MessageType.ERROR
    ) else null
    val trendingState = when (trendingState) {
        is TrendingState.Success -> trendingState.copy(
            showLoadingRefresh = false,
            showLoadingPage = false,
            loadingNextPageProgress = false
        )

        is TrendingState.Empty -> TrendingState.Empty(false)
        else -> TrendingState.Error()
    }
    return copy(
        snackbarState = snackbarState, trendingState = trendingState
    )
}

sealed interface TrendingState {
    data object Loading : TrendingState

    data class Empty(val showLoadingRefresh: Boolean = false) : TrendingState

    data class Error(
        @StringRes val messageError: Int? = null
    ) : TrendingState

    data class Success(
        val repoItems: List<RepoItem> = emptyList<RepoItem>().toImmutableList(),
        val showLoadingPage: Boolean = false,
        val loadingNextPageProgress: Boolean = false,
        val showLoadingRefresh: Boolean = false
    ) : TrendingState
}

fun TrendingState.successResponse(
    newRepos: List<RepoItem>,
    append: Boolean,
    end: Boolean
): TrendingState {
    val current = if (this is TrendingState.Success && !append) this.repoItems else emptyList()
    val result = current.plus(newRepos)
    return if (result.isEmpty()) {
        TrendingState.Empty()
    } else {
        TrendingState.Success(
            repoItems = result,
            showLoadingPage = !end,
            loadingNextPageProgress = false,
            showLoadingRefresh = false
        )
    }
}

fun TrendingState.canLoadNextPage(): Boolean {
    return this is TrendingState.Success && this.showLoadingPage && !this.loadingNextPageProgress
}

fun TrendingState.setLoadNextPageOnProgress(): TrendingState {
    return if (this is TrendingState.Success) {
        this.copy(loadingNextPageProgress = true)
    } else {
        this
    }
}

fun TrendingState.isLoadingPage(): Boolean {
    return this is TrendingState.Loading || (this is TrendingState.Success && this.showLoadingRefresh)
            || (this is TrendingState.Empty && this.showLoadingRefresh)
}

fun TrendingState.setLoadingPage(pullToRefresh: Boolean): TrendingState {
    return if (!pullToRefresh) {
        TrendingState.Loading
    } else if (this is TrendingState.Success) {
        copy(showLoadingRefresh = true)
    } else if (this is TrendingState.Empty) {
        copy(showLoadingRefresh = true)
    } else
        this
}

data class RepoItem(
    val id: String,
    val name: String,
    val owner: String,
    val description: String?,
    val star: Int,
    val language: String?,
    val watcher: Int,
    val repoStarState: RepoStarState,
    val bannerImage: String? = null,
    val ownerAvatar: String? = null
)

fun List<RepoDataModel>.toTrendingRepoItems(): List<RepoItem> {
    return this.map { repoDataModel ->
        RepoItem(
            repoDataModel.getId(),
            name = repoDataModel.name,
            owner = repoDataModel.owner.login,
            description = repoDataModel.description,
            star = repoDataModel.stargazersCount,
            language = repoDataModel.language,
            watcher = repoDataModel.watchersCount,
            repoStarState = RepoStarState.Success(repoDataModel.isStarred),
            ownerAvatar = repoDataModel.owner.avatarUrl
        )
    }
}

sealed interface RepoStarState {
    data class Loading(val starred: Boolean) : RepoStarState

    data class Success(val starred: Boolean) : RepoStarState
}

data class FilterState(
    val selectedTime: FilterTime = FilterTime.TODAY,
    val selectedLanguage: String? = null,
    val timeOptions: List<TimeOption> = listOf<TimeOption>().toImmutableList(),
    val languageOptionState: LanguageOptionState = LanguageOptionState.Loading,
    val filterCount: Int = 0
)

data class TimeOption(
    val time: FilterTime,
    val selected: Boolean
)

data class LanguageOption(
    val language: String,
    val selected: Boolean
)

sealed interface LanguageOptionState {
    data object Loading : LanguageOptionState

    data class Error(
        @StringRes val messageError: Int? = null
    ) : LanguageOptionState

    data class Success(
        val languageOptions: List<LanguageOption> = emptyList<LanguageOption>().toImmutableList()
    ) : LanguageOptionState
}

fun FilterState.changeFilter(
    selectedTime: FilterTime = this.selectedTime,
    selectedLanguage: String? = this.selectedLanguage,
): FilterState {
    var count = 0
    val timesOption = this.timeOptions.map {
        if (it.time == selectedTime)
            it.copy(selected = true)
        else
            it
    }
    if (selectedTime != FilterTime.TODAY) count += 1

    var languageOptionState = this.languageOptionState
    if (languageOptionState is LanguageOptionState.Success) {
        languageOptionState = LanguageOptionState.Success(languageOptionState.languageOptions.map {
            if (it.language == selectedLanguage)
                it.copy(selected = true)
            else
                it
        })
    }
    if (!selectedLanguage.isNullOrEmpty()) count += 1

    return FilterState(
        selectedTime,
        selectedLanguage,
        timesOption,
        languageOptionState,
        filterCount
    )
}