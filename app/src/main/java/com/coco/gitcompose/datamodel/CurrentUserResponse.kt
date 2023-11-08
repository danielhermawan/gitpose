package com.coco.gitcompose.datamodel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentUserResponse(
    val login: String,
    val id: Long,
    @Json(name = "avatar_url") val avatarUrl: String,
    val name: String,
    val email: String? = null,
    val bio: String? = null,
    val followers: Int,
    val following: Int,
    @Json(name = "total_private_repos") val totalPrivateRepos: Int = 0,
    @Json(name = "owned_private_repos") val ownedPrivateRepos: Int = 0,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String
)