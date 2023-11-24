package com.coco.gitcompose.core.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import com.squareup.moshi.Json
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_repository")
data class UserRepositoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    val private: Boolean,
    @ColumnInfo(name = "forked_from") val forkedFrom: String?,
    @ColumnInfo(name = "star_count") val starCount: Int,
    val language: String?,
    @ColumnInfo(name = "owner_name") val ownerName: String,
    val created: Long,
    val updated: Long,
    val pushed: Long,
    @Json(name = "html_url") val htmlUrl: String,
    val description: String? = null,
    val fork: Boolean = false,
    @ColumnInfo(name = "forks_count") val forksCount: Int,
    @ColumnInfo(name = "watchers_count") val watchersCount: Int,
    @ColumnInfo(name = "default_branch") val defaultBranch: String,
    @ColumnInfo(name = "open_issues_count") val openIssueCount: Int,
    @ColumnInfo(name = "is_template") val isTemplate: Boolean,
    val topics: String,
    val visibility: String,
    val ownerLogin: String,
    @ColumnInfo(name = "owner_avatar_url") val ownerAvatarUrl: String,
    @ColumnInfo(name = "parent_full_name") val parentFullName: String?,
)

@Dao
interface UserRepositoryDao {
    @Query("SELECT * FROM user_repository ORDER BY :sort DESC")
    fun getUserRepositoriesDesc(sort: String): Flow<List<UserRepositoryEntity>>

    @Query("SELECT * FROM user_repository ORDER BY :sort ASC")
    fun getUserRepositoriesAsc(sort: String): Flow<List<UserRepositoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepository(repositories: List<UserRepositoryEntity>)

    @Query("DELETE FROM user_repository")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceDataInTransaction(repositories: List<UserRepositoryEntity>) {
        clearAll()
        insertRepository(repositories)
    }
}
