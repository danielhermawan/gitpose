package com.coco.gitcompose.screen.landing

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.MessageType
import com.coco.gitcompose.core.ui.SnackbarState
import com.coco.gitcompose.datamodel.CurrentUser
import com.coco.gitcompose.usecase.GithubAuthUseCase
import com.coco.gitcompose.usecase.GithubUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val githubAuthUseCase: GithubAuthUseCase,
    private val githubUserUserCase: GithubUserUseCase,
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
                    it.copy(
                        navItems = getLandingNavItems().toImmutableList(),
                        appBarTitle = getAppBarTitle()
                    )
                }
                githubUserUserCase.getStreamCurrentUser(true)
                    .catch { ex ->
                        Log.e(LandingViewModel::class.simpleName, ex.message, ex)
                        _uiState.update {
                            it.copy(
                                snackbarState = SnackbarState(
                                    R.string.landing_error_load_user_data,
                                    MessageType.ERROR
                                )
                            )
                        }
                    }
                    .collect { currentUser ->
                        updateCurrentUserData(currentUser)
                    }
            }
        }
    }

    fun onSnackbarEvent(snackbarState: SnackbarState?) {
        _uiState.update {
            it.copy(snackbarState = snackbarState)
        }
    }

    fun onSnackbarMessageShown() {
        _uiState.update {
            it.copy(snackbarState = null)
        }
    }

    fun onNavigationItemClick(selectedNav: LandingNav) {
        val navItems = _uiState.value.navItems.map { navItem ->
            navItem.copy(selected = selectedNav == navItem.id)
        }.toImmutableList()
        _uiState.update {
            it.copy(
                navItems = navItems,
                appBarTitle = getAppBarTitle(selectedNav),
                selectedTab = selectedNav
            )
        }
    }

    private fun updateCurrentUserData(currentUser: CurrentUser) {
        _uiState.update {
            it.copy(
                profilePictureUrl = currentUser.avatarUrl
            )
        }
    }

    private fun getLandingNavItems(selectedNav: LandingNav = LandingNav.HOME): List<NavItem> {
        return listOf(
            NavItem(
                id = LandingNav.HOME,
                appBarTitle = R.string.landing_tab_title_home,
                icon = R.drawable.ic_home_24,
                label = R.string.landing_tab_title_home,
                selected = selectedNav == LandingNav.HOME,
                selectedIcon = R.drawable.ic_home_filled_24
            ),
            NavItem(
                id = LandingNav.NOTIFICATIONS,
                appBarTitle = R.string.landing_tab_title_notification,
                icon = R.drawable.ic_notifications_24,
                label = R.string.landing_tab_title_notification,
                selected = selectedNav == LandingNav.NOTIFICATIONS,
                selectedIcon = R.drawable.ic_notifications_filled_24
            ),
            NavItem(
                id = LandingNav.EXPLORE,
                appBarTitle = R.string.landing_tab_title_explore,
                icon = R.drawable.ic_explore_24,
                label = R.string.landing_tab_title_explore,
                selected = selectedNav == LandingNav.EXPLORE,
                selectedIcon = R.drawable.ic_explore_filled_24px
            ),
            NavItem(
                id = LandingNav.PROFILE,
                appBarTitle = R.string.landing_tab_title_profile,
                icon = R.drawable.ic_person_24,
                label = R.string.landing_tab_title_profile,
                selected = selectedNav == LandingNav.PROFILE,
                selectedIcon = R.drawable.ic_person_filled_24
            )
        )
    }

    private fun getAppBarTitle(selectedNav: LandingNav = LandingNav.HOME): Int {
        return when (selectedNav) {
            LandingNav.HOME -> R.string.landing_tab_title_home
            LandingNav.PROFILE -> R.string.landing_tab_title_profile
            LandingNav.EXPLORE -> R.string.landing_tab_title_explore
            LandingNav.NOTIFICATIONS -> R.string.landing_tab_title_notification
        }
    }

}