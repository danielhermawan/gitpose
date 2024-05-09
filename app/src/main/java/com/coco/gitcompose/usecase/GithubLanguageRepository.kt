package com.coco.gitcompose.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface GithubLanguageRepository {
    fun observeLanguages(): Flow<List<String>>

}

public class DefaultGithubLanguageRepository @Inject constructor() : GithubLanguageRepository {
    override fun observeLanguages(): Flow<List<String>> {
        return flowOf(listOf("java", "c++", "go", "javascript", "python"))
    }

}