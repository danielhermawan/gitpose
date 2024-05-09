package com.coco.gitcompose.core.datamodel

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchRepoResponse(
    val totalCount: Int,
    val incompleteResult: Boolean,
    val items: List<RepoDataModel>
)