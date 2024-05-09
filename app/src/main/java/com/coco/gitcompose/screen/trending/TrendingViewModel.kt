package com.coco.gitcompose.screen.trending

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coco.gitcompose.R
import com.coco.gitcompose.usecase.GithubLanguageRepository
import com.coco.gitcompose.usecase.GithubSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class TrendingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val githubSearchRepository: GithubSearchRepository,
    private val githubLanguageRepository: GithubLanguageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TrendingUiState())
    private var currentPage = 0

    val uiState: StateFlow<TrendingUiState> = _uiState.asStateFlow()

    fun init() {
        setupFilterTime(
            filterTimes = listOf(
                FilterTime.TODAY,
                FilterTime.THIS_WEEK,
                FilterTime.THIS_MONTH
            )
        )

        viewModelScope.launch {
            githubLanguageRepository.observeLanguages()
                .catch {
                    setupFilterLanguage(isError = true)
                }
                .collect {
                    setupFilterLanguage(it)
                }
        }

        viewModelScope.launch {
            loadTrending()
        }
    }

    fun onSnackbarMessageShown() {
        _uiState.update {
            it.copy(snackbarState = null)
        }
    }

    /**
     * Intent Area Begin
     */

    fun changeTime(selectedTime: FilterTime) {
        changeFilter(selectedTime = selectedTime)
    }

    fun changeLanguage(language: String) {
        changeFilter(selectedLanguage = language)
    }

    fun clearFilter() {
        changeFilter(FilterTime.TODAY, null)
    }

    fun loadNextPage() {
        loadTrending(nextPage = true)
    }

    fun reloadPage() {
        loadTrending()
    }

    fun pullToRefresh() {
        loadTrending(pullToRefresh = true)
    }

    fun starRepo(repoItem: RepoItem) {
        _uiState.update {
            it.copy()
        }
        viewModelScope.launch {
            try {

            } catch (exception: Exception) {

            }
        }
    }


    /**
     * Intent Area End
     */

    /**
     * Action Area Begin
     */
    private fun changeFilter(
        selectedTime: FilterTime = _uiState.value.filterState.selectedTime,
        selectedLanguage: String? = _uiState.value.filterState.selectedLanguage,
    ) {
        _uiState.update {
            it.copy(filterState = it.filterState.changeFilter(selectedTime, selectedLanguage))
        }

        loadTrending(nextPage = false, pullToRefresh = false)
    }

    //todo: handle concurrent pagination issue
    private fun loadTrending(nextPage: Boolean = false, pullToRefresh: Boolean = false) {
        if (nextPage && uiState.value.trendingState.canLoadNextPage()) return
        if (uiState.value.trendingState.isLoadingPage()) return
        _uiState.update {
            if (nextPage) {
                it.copy(trendingState = it.trendingState.setLoadingPage(pullToRefresh))
            } else {
                it.copy(trendingState = it.trendingState.setLoadNextPageOnProgress())
            }
        }

        viewModelScope.launch {
            try {
                val response = githubSearchRepository.getTrendingRepository(
                    uiState.value.filterState.selectedTime.getDay(),
                    page = if (nextPage) currentPage + 1 else 1
                )
                currentPage = if (nextPage) currentPage + 1 else 1
                _uiState.update {
                    it.copy(
                        trendingState = it.trendingState.successResponse(
                            response.items.toTrendingRepoItems(),
                            nextPage,
                            response.items.isNotEmpty()
                        )
                    )
                }
            } catch (ex: Exception) {
                if (ex !is CancellationException) {
                    _uiState.update { it.showError() }
                }
            }
        }
    }

    /**
     * Action Area End
     */
    private fun setupFilterTime(
        filterTimes: List<FilterTime>
    ) {
        val timesOption = filterTimes.map {
            TimeOption(it, false)
        }
        _uiState.update {
            it.copy(
                filterState = FilterState(
                    selectedTime = FilterTime.TODAY,
                    timeOptions = timesOption
                )
            )
        }
    }

    private fun setupFilterLanguage(
        languages: List<String> = emptyList(),
        isError: Boolean = false
    ) {
        val languageOptions = if (isError) {
            LanguageOptionState.Error(R.string.common_server_error)
        } else {
            LanguageOptionState.Success(languages.map {
                LanguageOption(it, false)
            })
        }
        _uiState.update {
            it.copy(
                filterState = FilterState(
                    selectedLanguage = null,
                    languageOptionState = languageOptions
                )
            )
        }
    }
}