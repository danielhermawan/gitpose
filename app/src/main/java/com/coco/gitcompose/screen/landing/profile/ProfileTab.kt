package com.coco.gitcompose.screen.landing.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.coco.gitcompose.core.ui.theme.Black40
import com.coco.gitcompose.core.ui.theme.GitposeTheme
import com.coco.gitcompose.core.ui.theme.Pink40
import com.coco.gitcompose.core.ui.theme.Yellow70
import com.coco.gitcompose.screen.landing.LandingNav

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
        ProfileScreen(
            modifier = modifier,
            uiState = uiState,
            onLogoutClick = {
                viewModel.logout()
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

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {},
    uiState: ProfileUiState = ProfileUiState()
) {
    Column(
        modifier = modifier,
    ) {
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
            onLogoutClick = onLogoutClick
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
    onLogoutClick: () -> Unit = {},
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
                //todo handle loading
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
                            language = repo.language
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            )


            ProfileMenu(modifier = Modifier.padding(12.dp))
        }
    }
}

@Composable
fun ProfileMenu(
    modifier: Modifier = Modifier,
    backgroundIconColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    menuName: String = "Repositories",
    infoCount: Int = 0,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.clickable {
            onClick()
        }.then(modifier)
    ) {
        Surface(
            color = backgroundIconColor,
            contentColor = MaterialTheme.colorScheme.primaryContainer,
            shape = ShapeDefaults.ExtraSmall,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_book_24),
                modifier = Modifier
                    .padding(6.dp)
                    .size(18.dp),
                contentDescription = "Icon repository",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primaryContainer)
            )
        }

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

        Text(
            text = "$infoCount",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(CenterVertically),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun RepoCard(
    modifier: Modifier = Modifier,
    pictureUrl: String = "",
    loginName: String = "",
    repoName: String = "",
    repoDescription: String? = null,
    starCount: Int = 0,
    language: String? = null
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = ShapeDefaults.ExtraSmall,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background)
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

/*
@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    GitposeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileTab()
        }
    }
}*/
