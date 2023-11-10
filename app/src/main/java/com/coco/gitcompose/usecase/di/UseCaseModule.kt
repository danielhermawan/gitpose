package com.coco.gitcompose.usecase.di

import com.coco.gitcompose.usecase.DefaultAuthUseCase
import com.coco.gitcompose.usecase.DefaultGithubUserUserCase
import com.coco.gitcompose.usecase.GithubAuthUseCase
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
        userUsecase: DefaultGithubUserUserCase
    ): GithubUserUseCase
}
