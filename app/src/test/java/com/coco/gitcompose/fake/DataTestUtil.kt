package com.coco.gitcompose.fake

import com.coco.gitcompose.core.datamodel.RepoDataModel
import com.coco.gitcompose.core.datamodel.RepoOwnerDataModel


fun createDummyPublicRepos(count: Int = 10): MutableList<RepoDataModel> {
    return (1..count).map {
        createPublicDummyRepo("git$it", it.toChar().toString())
    }.toMutableList()
}

fun createDummyStarredRepo(count: Int = 10): MutableSet<String> {
    return (1..count).map {
        "git$it/${it.toChar()}"
    }.toMutableSet()
}

fun createPublicDummyRepo(ownerLogin: String, name: String): RepoDataModel {
    return RepoDataModel(
        "${ownerLogin}/${name}",
        name,
        name,
        false,
        htmlUrl = "html",
        description = null,
        fork = false,
        language = "Java",
        forksCount = 10,
        stargazersCount = 10,
        watchersCount = 10,
        defaultBranch = "master",
        openIssueCount = 10,
        isTemplate = false,
        topics = emptyList(),
        visibility = "public",
        RepoOwnerDataModel(ownerLogin, "avatar"),
        "",
        "",
        "",
        null,
        false
    )
}