package com.coco.gitcompose.screen.landing.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.SnackbarState
import com.coco.gitcompose.core.ui.shimmeringLoadingAnimation
import com.coco.gitcompose.core.ui.theme.Black40
import com.coco.gitcompose.core.ui.theme.GitposeTheme
import com.coco.gitcompose.core.ui.theme.Orange40
import com.coco.gitcompose.core.ui.theme.Pink40
import com.coco.gitcompose.core.ui.theme.Red50
import com.coco.gitcompose.core.ui.theme.White80
import com.coco.gitcompose.core.ui.theme.Yellow70
import com.coco.gitcompose.screen.landing.LandingNav

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileTab(
    modifier: Modifier = Modifier,
    selectedTab: LandingNav = LandingNav.PROFILE,
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit = {},
    onSnackbarEvent: (SnackbarState?) -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (selectedTab == LandingNav.PROFILE) {
        val pullRefreshState =
            rememberPullRefreshState(uiState.isRefreshing, { viewModel.refresh() })

        ProfileScreen(
            modifier = modifier,
            uiState = uiState,
            pullToRefreshState = pullRefreshState,
            onLogoutClick = {
                viewModel.logout()
            },
            onRepoCLick = {
                //todo: navigate to repo detail
            },
            onRepoMenuCLick = {
                //todo: navigate to user repo list
            },
            onOrganizationClick = {

            },
            onStarredClick = {
                //todo: navigate to user starred re[p
            }
        )

        LaunchedEffect(uiState.snackbarState) {
            onSnackbarEvent(uiState.snackbarState)
        }

        LaunchedEffect(uiState.logoutSuccess) {
            if (uiState.logoutSuccess) {
                onLogoutSuccess()
            }
        }

        LaunchedEffect(selectedTab) {
            viewModel.onTabSelected()
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState = ProfileUiState(),
    pullToRefreshState: PullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = {}),
    onLogoutClick: () -> Unit = {},
    onRepoCLick: (String) -> Unit = {},
    onRepoMenuCLick: () -> Unit = {},
    onOrganizationClick: () -> Unit = {},
    onStarredClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .pullRefresh(pullToRefreshState)
            .verticalScroll(rememberScrollState())
    ) {
        Column {
            ProfileSection(
                modifier = Modifier.fillMaxWidth(),
                profilePictureUrl = uiState.profilePictureUrl,
                name = uiState.name,
                loginName = uiState.username,
                followerCount = uiState.followers
            )

            Spacer(modifier = Modifier.height(16.dp))

            RepoSection(
                modifier = Modifier.fillMaxWidth(),
                recentRepoUiState = uiState.recentRepos,
                repositoryCount = uiState.totalRepo,
                organizationCount = uiState.totalOrganizations,
                onLogoutClick = onLogoutClick,
                onRepoCLick = onRepoCLick,
                onRepoMenuCLick = onRepoMenuCLick,
                onOrganizationClick = onOrganizationClick,
                onStarredClick = onStarredClick
            )
        }

        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = pullToRefreshState,
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun ProfileSection(
    modifier: Modifier = Modifier,
    profilePictureUrl: String = "",
    name: String = "",
    loginName: String = "",
    followerCount: Int = 0
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier,
        shadowElevation = 2.dp
    ) {

        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = modifier.height(IntrinsicSize.Min)) {
                AsyncImage(
                    model = profilePictureUrl,
                    contentDescription = "Your profile picture",
                    placeholder = painterResource(id = R.drawable.ic_placeholder_24),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = loginName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_person_24),
                    contentDescription = "Person Icon",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Black40)
                )

                Text(
                    text = "$followerCount",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .align(CenterVertically)
                )

                Text(
                    text = stringResource(R.string.landing_profile_followers_count),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RepoSection(
    modifier: Modifier = Modifier,
    recentRepoUiState: RecentRepoUiState = RecentRepoUiState.Loading,
    repositoryCount: Int = 0,
    organizationCount: Int = 0,
    onLogoutClick: () -> Unit = {},
    onRepoCLick: (String) -> Unit = {},
    onRepoMenuCLick: () -> Unit = {},
    onOrganizationClick: () -> Unit = {},
    onStarredClick: () -> Unit = {},
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_star_24),
                    contentDescription = "Star Icon",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Black40)
                )

                Text(
                    text = stringResource(R.string.landing_profiler_section_popular),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .align(CenterVertically)
                )
            }

            if (recentRepoUiState is RecentRepoUiState.Success) {
                //todo: error
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = recentRepoUiState.recentRepos,
                        key = { repo -> repo.id }
                    ) { repo ->
                        RepoCard(
                            modifier = Modifier
                                .width(250.dp) //todo: change to ratio
                                .animateItemPlacement(),
                            pictureUrl = repo.profilePictureUrl,
                            loginName = repo.ownerName,
                            repoName = repo.name,
                            repoDescription = repo.description,
                            starCount = repo.starCount,
                            language = repo.language,
                            link = repo.link,
                            onRepoCLick = onRepoCLick
                        )
                    }
                }
            } else if (recentRepoUiState is RecentRepoUiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LoadingRepo(
                        modifier = Modifier.width(250.dp)
                    )
                    LoadingRepo(
                        modifier = Modifier.width(250.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            )

            ProfileMenu(
                modifier = Modifier.padding(12.dp),
                menuName = stringResource(R.string.landing_profile_menu_repositories),
                icon = R.drawable.ic_book_24,
                infoCount = repositoryCount,
                onClick = onRepoMenuCLick
            )

            ProfileMenu(
                modifier = Modifier.padding(12.dp),
                backgroundIconColor = Orange40,
                menuName = stringResource(R.string.landing_profile_menu_organization),
                icon = R.drawable.ic_organization_24,
                infoCount = organizationCount,
                onClick = onOrganizationClick
            )

            ProfileMenu(
                modifier = Modifier.padding(12.dp),
                backgroundIconColor = Yellow70,
                menuName = stringResource(R.string.landing_profile_menu_starred),
                infoCount = null,
                icon = R.drawable.ic_star_24,
                onClick = onStarredClick
            )

            ProfileMenu(
                modifier = Modifier.padding(12.dp),
                backgroundIconColor = Red50,
                menuName = stringResource(R.string.landing_profile_menu_logout),
                infoCount = null,
                icon = R.drawable.ic_logout_24,
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
fun LoadingRepo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = ShapeDefaults.Small
                )
                .shimmeringLoadingAnimation()
        )

        Spacer(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(0.4f)
                .height(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = ShapeDefaults.Small
                )
                .shimmeringLoadingAnimation()
        )

        Spacer(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(0.9f)
                .height(20.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = ShapeDefaults.Small
                )
                .shimmeringLoadingAnimation()
        )

        Spacer(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(0.5f)
                .height(14.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = ShapeDefaults.Small
                )
                .shimmeringLoadingAnimation()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoCard(
    modifier: Modifier = Modifier,
    link: String = "",
    pictureUrl: String = "",
    loginName: String = "",
    repoName: String = "",
    repoDescription: String? = null,
    starCount: Int = 0,
    language: String? = null,
    onRepoCLick: (String) -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = ShapeDefaults.ExtraSmall,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background),
        onClick = { onRepoCLick(link) }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row {
                AsyncImage(
                    model = pictureUrl,
                    contentDescription = "Owner repo profile picture",
                    placeholder = painterResource(id = R.drawable.ic_placeholder_24),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = loginName,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(CenterVertically)
                )
            }

            Text(
                text = repoName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = repoDescription ?: " ",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontStyle = if (repoDescription.isNullOrEmpty()) FontStyle.Italic else FontStyle.Normal
            )

            Row(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_star_filled),
                    contentDescription = "Star Icon",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(Yellow70)
                )

                Text(
                    text = "$starCount",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(CenterVertically)
                )

                if (!language.isNullOrEmpty()) {
                    Spacer(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(12.dp)
                            .background(Pink40, CircleShape)
                            .align(CenterVertically)
                    ) //todo: generate color unique per language

                    Text(
                        text = language,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .align(CenterVertically)
                    )
                }
            }
        }
    }

}

@Composable
fun ProfileMenu(
    modifier: Modifier = Modifier,
    backgroundIconColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    menuName: String = "",
    infoCount: Int? = 0,
    @DrawableRes icon: Int = R.drawable.ic_book_24,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .then(modifier)
    ) {
        Image(
            painter = painterResource(icon),
            modifier = Modifier
                .background(backgroundIconColor, ShapeDefaults.ExtraSmall)
                .padding(6.dp)
                .size(18.dp),
            contentDescription = "Icon repository",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primaryContainer)
        )

        Text(
            text = menuName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
                .align(CenterVertically),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (infoCount != null) {
            Text(
                text = "$infoCount",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(CenterVertically),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSectionPreview() {
    GitposeTheme {
        ProfileSection(
            modifier = Modifier.fillMaxWidth(),
            profilePictureUrl = "",
            name = "Daniel Hermawan",
            loginName = "danielhermawan"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RepoSectionPreview() {
    GitposeTheme {
        RepoSection(
            modifier = Modifier.fillMaxWidth(),
            /*recentRepoUiState = RecentRepoUiState.Success(
                listOf(
                    RecentRepoViewModel(
                        id = "1",
                        link = "danielhermawan/gitpose",
                        profilePictureUrl = "",
                        ownerName = "danielhermawan",
                        name = "gitpose",
                        description = "Github application that used github api and compose",
                        starCount = 2,
                        language = "Kotlin",
                        color = Pink40
                    ),
                    RecentRepoViewModel(
                        id = "1",
                        link = "danielhermawan/usr-account",
                        profilePictureUrl = "",
                        ownerName = "danielhermawan",
                        name = "usr-account",
                        description = "Github application that used user api and compose",
                        starCount = 2,
                        language = "Kotlin",
                        color = Pink40
                    )
                )
            )*/
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RepoLoadingPreview() {
    GitposeTheme {
        LoadingRepo(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(White80)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun RepoCardPreview() {
    GitposeTheme {
        RepoCard(
            modifier = Modifier.width(240.dp),
            pictureUrl = "",
            loginName = "danielhermawan",
            repoName = "Gitpose",
            repoDescription = "Mobile application that used github application using compose as view",
            starCount = 2,
            language = "Java"
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    GitposeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileScreen()
        }
    }
}
