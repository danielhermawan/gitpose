package com.coco.gitcompose.datamodel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RepoDataModel(
    val id: String,
    val name: String,
    @Json(name = "full_name") val fullName: String,
    val private: Boolean,
    @Json(name = "html_url") val htmlUrl: String,
    val description: String? = null,
    val fork: Boolean = false,
    val language: String? = null,
    @Json(name = "forks_count") val forksCount: Int,
    @Json(name = "stargazers_count") val stargazersCount: Int,
    @Json(name = "watchers_count") val watchersCount: Int,
    @Json(name = "default_branch") val defaultBranch: String,
    @Json(name = "open_issues_count") val openIssueCount: Int,
    @Json(name = "is_template") val isTemplate: Boolean,
    val topics: List<String> = emptyList(),
    val visibility: String,
    val owner: RepoOwnerDataModel,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "pushed_at") val pushedAt: String
)

@JsonClass(generateAdapter = true)
data class RepoOwnerDataModel(
    val login: String,
    val id: Int,
    @Json(name = "avatar_url") val avatarUrl: String,
)