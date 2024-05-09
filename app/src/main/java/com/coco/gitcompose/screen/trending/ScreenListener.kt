package com.coco.gitcompose.screen.trending

interface ScreenListener {
    fun onPullToRefresh()
    fun loadNextPage()
    fun reloadPage()
    fun changeTime(time: FilterTime)
    fun changeLanguage(language: String)
    fun clearFilter()

    fun starRepo(repoItem: RepoItem)
}