package com.coco.gitcompose.screen.trending

import androidx.lifecycle.SavedStateHandle
import com.coco.gitcompose.fake.MainCoroutineRule
import com.coco.gitcompose.usecase.GithubLanguageRepository
import com.coco.gitcompose.usecase.GithubSearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TrendingViewModelTest {
    // Subject under test
    private lateinit var trendingViewModel: TrendingViewModel

    // Test Dependency
    private lateinit var languageRepository: GithubLanguageRepository
    private lateinit var githubSearchRepository: GithubSearchRepository


    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        // init(this, relaxUnitFun = true)

        trendingViewModel = TrendingViewModel(
            SavedStateHandle(),
            githubSearchRepository,
            languageRepository
        )
    }

    @Test
    fun init_setupFilterAndLoadRepo() = runTest {

    }

}