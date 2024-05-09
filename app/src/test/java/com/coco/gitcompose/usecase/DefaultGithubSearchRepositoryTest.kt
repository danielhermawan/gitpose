package com.coco.gitcompose.usecase

import com.coco.gitcompose.core.remote.GithubService
import com.coco.gitcompose.core.util.DateUtil
import com.coco.gitcompose.fake.FakeGithubService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DefaultGithubSearchRepositoryTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private var testDispatcher = UnconfinedTestDispatcher()

    // Test Dependency
    private lateinit var fakeGithubService: GithubService

    // Class under test
    private lateinit var repository: DefaultGithubSearchRepository

    @ExperimentalCoroutinesApi
    @Before
    fun init() {
        fakeGithubService = FakeGithubService()


        repository = DefaultGithubSearchRepository(
            fakeGithubService, testDispatcher, DateUtil()
        )
    }

    @Test
    fun getTrendingRepository_getListRepository() = runTest(testDispatcher) {
        val trendingRepository = repository.getTrendingRepository(1, 5, 1)

        assertEquals(10, trendingRepository.items.count())
        assertEquals(3, trendingRepository.items.count { it.isStarred })


    }

}