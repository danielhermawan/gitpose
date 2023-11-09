package com.coco.gitcompose.screen.landing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coco.gitcompose.R
import com.coco.gitcompose.usecase.GithubAuthUseCase
import com.coco.gitcompose.usecase.GithubUserUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

data class LandingUiState(
    val isLogin: Boolean = true,
    val logoutSuccess: Boolean = false,
    val appBarTitle: Int = R.string.landing_tab_title_home,
    val navItems: ImmutableList<NavItem> = emptyList<NavItem>().toImmutableList()
)

enum class LandingNav {
    HOME, NOTIFICATIONS, EXPLORE, PROFILE
}

data class NavItem(
    val id: LandingNav = LandingNav.HOME,
    val appBarTitle: Int = R.string.landing_tab_title_home,
    val icon: Int = R.drawable.ic_home_24,
    val selectedIcon: Int = R.drawable.ic_home_filled_24,
    val label: Int = R.string.landing_tab_title_home,
    val selected: Boolean = false
)

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val githubAuthUseCase: GithubAuthUseCase,
    private val githubUserUserCase: GithubUserUserCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LandingUiState())
    val uiState: StateFlow<LandingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val isLogin = githubAuthUseCase
                .isLogin()
                .catch { emit(false) }
                .first()
            _uiState.update {
                it.copy(isLogin = isLogin)
            }

            if (isLogin) {
                _uiState.update {
                    it.copy(navItems = getLandingNavItems().toImmutableList())
                }

            }
        }
    }

    fun onNavigationItemClick(selectedNav: LandingNav) {

    }

    fun logout() {
        viewModelScope.launch {
            githubAuthUseCase.logout()
            _uiState.update {
                it.copy(logoutSuccess = true)
            }
        }
    }

    private fun getLandingNavItems(selectedNav: LandingNav = LandingNav.HOME): List<NavItem> {
        return listOf(
            NavItem(
                id = LandingNav.HOME, appBarTitle = R.string.landing_tab_title_home,
                icon = R.drawable.ic_home_24, label = R.string.landing_tab_title_home,
                selected = selectedNav == LandingNav.HOME, selectedIcon = R.drawable.ic_home_filled_24
            ),
            NavItem(
                id = LandingNav.NOTIFICATIONS, appBarTitle = R.string.landing_tab_title_notification,
                icon = R.drawable.ic_notifications_24, label = R.string.landing_tab_title_notification,
                selected =selectedNav == LandingNav.NOTIFICATIONS, selectedIcon = R.drawable.ic_notifications_filled_24
            ),
            NavItem(
                id = LandingNav.EXPLORE, appBarTitle = R.string.landing_tab_title_explore,
                icon = R.drawable.ic_explore_24, label = R.string.landing_tab_title_explore,
                selected =selectedNav == LandingNav.EXPLORE, selectedIcon = R.drawable.ic_explore_filled_24px
            ),
            NavItem(
                id = LandingNav.PROFILE, appBarTitle = R.string.landing_tab_title_profile,
                icon = R.drawable.ic_person_24, label = R.string.landing_tab_title_profile,
                selected =selectedNav == LandingNav.PROFILE, selectedIcon = R.drawable.ic_person_filled_24
            )
        )
    }

}