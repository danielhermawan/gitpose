package com.coco.gitcompose.usecase.di

import com.coco.gitcompose.usecase.DefaultAuthUseCase
import com.coco.gitcompose.usecase.DefaultGithubLanguageRepository
import com.coco.gitcompose.usecase.DefaultGithubRepositoryUseCase
import com.coco.gitcompose.usecase.DefaultGithubSearchRepository
import com.coco.gitcompose.usecase.DefaultGithubUserUseCase
import com.coco.gitcompose.usecase.GithubAuthUseCase
import com.coco.gitcompose.usecase.GithubLanguageRepository
import com.coco.gitcompose.usecase.GithubRepositoryUseCase
import com.coco.gitcompose.usecase.GithubSearchRepository
import com.coco.gitcompose.usecase.GithubUserUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Singleton
    @Binds
    fun bindsLoginRepository(
        gitRepositoryRepository: DefaultAuthUseCase
    ): GithubAuthUseCase

    @Singleton
    @Binds
    fun bindsUserUsecase(
        userUsecase: DefaultGithubUserUseCase
    ): GithubUserUseCase

    @Singleton
    @Binds
    fun bindsRepositoryUsecase(
        githubRepositoryUseCase: DefaultGithubRepositoryUseCase
    ): GithubRepositoryUseCase

    @Singleton
    @Binds
    fun bindsSearchRepository(
        githubSearchRepository: DefaultGithubSearchRepository
    ): GithubSearchRepository

    @Singleton
    @Binds
    fun bindsLanguageRepository(
        githubLanguageRepository: DefaultGithubLanguageRepository
    ): GithubLanguageRepository
}
