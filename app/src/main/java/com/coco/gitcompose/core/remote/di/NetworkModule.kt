package com.coco.gitcompose.core.remote.di

import com.coco.gitcompose.BuildConfig
import com.coco.gitcompose.core.remote.GithubAuthService
import com.coco.gitcompose.core.remote.GithubService
import com.coco.gitcompose.core.remote.GithubTokenInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class OkhttpClient(val dispatchers: GitposeOkhttpClient)

enum class GitposeOkhttpClient {
    GITHUB_API,
    GITHUB_AUTH,
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @OkhttpClient(GitposeOkhttpClient.GITHUB_API)
    fun githubApiCallFactory(githubTokenInterceptor: GithubTokenInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .addInterceptor(githubTokenInterceptor)
            .build()

    @Provides
    @Singleton
    @OkhttpClient(GitposeOkhttpClient.GITHUB_AUTH)
    fun githubAuthCallFactory(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .build()

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun githubService(
        @OkhttpClient(GitposeOkhttpClient.GITHUB_API) okHttpClient: OkHttpClient, moshi: Moshi
    ): GithubService = Retrofit.Builder()
        .baseUrl(GithubService.GITHUB_BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(moshi)
        )
        .client(okHttpClient)
        .build()
        .create(GithubService::class.java)

    @Provides
    @Singleton
    fun githubAuthService(
        @OkhttpClient(GitposeOkhttpClient.GITHUB_AUTH) okHttpClient: OkHttpClient, moshi: Moshi
    ): GithubAuthService = Retrofit.Builder()
        .baseUrl(GithubAuthService.BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(moshi)
        )
        .client(okHttpClient)
        .build()
        .create(GithubAuthService::class.java)

}