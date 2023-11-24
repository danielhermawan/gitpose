package com.coco.gitcompose.screen.userRepository

interface ScreenListener {
    fun onBackPressed() {}

    fun onFilterTypeSelected(repoTypeLabel: RepoTypeLabel) {}

    fun onFilterSortSelected(sortLabel: SortLabel) {}

    fun onReloadPage() {}

    fun onLoadNextPage() {}

    fun onResetFilterClick() {}

    fun onRepositoryClick() {}

    fun onPullToRefresh() {}

}