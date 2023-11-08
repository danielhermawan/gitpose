package com.coco.gitcompose.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.coco.gitcompose.core.common.ApplicationScope
import com.coco.gitcompose.core.common.Dispatcher
import com.coco.gitcompose.core.common.GitposeDispatchers
import com.coco.gitcompose.core.datastore.GithubTokenSerializer
import com.coco.gitcompose.data.GithubToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideGithubTokenDataStore(
        @ApplicationContext context: Context,
        githubTokenSerializer: GithubTokenSerializer,
        @Dispatcher(GitposeDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
    ): DataStore<GithubToken> {
        return DataStoreFactory.create(
            githubTokenSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher)
        ) {
            context.dataStoreFile("user_preferences.pb")
        }
    }
}