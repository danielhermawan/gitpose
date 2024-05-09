package com.coco.gitcompose.screen.trending

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.BaseScreen
import com.coco.gitcompose.core.ui.theme.Black80
import com.coco.gitcompose.core.ui.theme.GitposeTheme
import com.coco.gitcompose.core.ui.theme.Yellow70
import com.coco.gitcompose.core.util.languageToColor
import com.coco.gitcompose.core.util.toColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrendingActivity : ComponentActivity() {
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, TrendingActivity::class.java)
        }
    }

    private val viewModel: TrendingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init()
        setContent {
            GitposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    val screenListener = remember {
                        object : ScreenListener {
                            override fun onPullToRefresh() {
                                viewModel.pullToRefresh()
                            }

                            override fun loadNextPage() {
                                viewModel.loadNextPage()
                            }

                            override fun reloadPage() {
                                viewModel.reloadPage()
                            }

                            override fun changeTime(time: FilterTime) {
                                viewModel.changeTime(time)
                            }

                            override fun changeLanguage(language: String) {
                                viewModel.changeLanguage(language)
                            }

                            override fun clearFilter() {
                                viewModel.clearFilter()
                            }

                            override fun starRepo(repoItem: RepoItem) {
                                viewModel.starRepo(repoItem)
                            }

                        }
                    }

                    BaseScreen(
                        snackbarState = uiState.snackbarState,
                        topBarTitle = stringResource(id = R.string.trending_screen_title),
                        onSnackbarShown = {
                            viewModel.onSnackbarMessageShown()
                        },
                        onBackPressed = {
                            finish()
                        }) { modifier ->
                        TrendingScreen(
                            modifier = modifier,
                            uiState = uiState,
                            screenListener = screenListener
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingScreen(
    modifier: Modifier,
    uiState: TrendingUiState,
    screenListener: ScreenListener
) {
    Column(modifier = modifier) {
        FilterBar(
            modifier = Modifier.fillMaxWidth(),
            filterState = uiState.filterState,
            onChangeTime = { screenListener.changeTime(it) },
            onChangeLanguage = { screenListener.changeLanguage(it) },
            onClearFilter = { screenListener.clearFilter() }
        )
    }
}

@Composable
fun FilterBar(
    modifier: Modifier,
    filterState: FilterState,
    onChangeTime: (FilterTime) -> Unit,
    onChangeLanguage: (String) -> Unit,
    onClearFilter: () -> Unit
) {

}

@Composable
fun TrendingRepositorySection(
    modifier: Modifier,
    trendingState: TrendingState,
    onPullToRefresh: () -> Unit,
    onReloadPage: () -> Unit,
    onLoadNextPage: () -> Unit,
    onClickStar: (RepoItem) -> Unit
) {

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RepoListSection(
    modifier: Modifier = Modifier,
    recentRepos: List<RepoItem>,
    pullRefreshState: PullRefreshState,
    enabledPullToRefresh: Boolean,
    showLoadingNextPage: Boolean,
    loadNextPageOnProgress: Boolean,
    onStarClick: (RepoItem) -> Unit,
    onLoadNextPage: () -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(recentRepos, key = { repo ->
            repo.id
        }) {
            RepoItem(Modifier.fillMaxWidth(), it, onStarClick = onStarClick)
        }

        if (showLoadingNextPage) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 24.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        } else {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.github_logo),
                            contentDescription = null
                        )
                        Text(
                            text = "That's all for now",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Text(
                            text = "Check back later to see what repositories are currently trending",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RepoItem(
    modifier: Modifier = Modifier,
    repository: RepoItem,
    onStarClick: (RepoItem) -> Unit
) {
    Column {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = modifier,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = modifier.height(IntrinsicSize.Min)) {
                    AsyncImage(
                        model = repository.ownerAvatar,
                        contentDescription = "Owner repo profile picture",
                        placeholder = painterResource(id = R.drawable.ic_placeholder_24),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = repository.owner, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = repository.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = repository.description
                            ?: stringResource(id = R.string.landing_repo_empty_description),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(top = 12.dp, end = 16.dp)
                            .weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Image(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterVertically),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.ic_placeholder_24),
                        contentDescription = null,
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_star_24),
                        contentDescription = "star",
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterVertically),
                        colorFilter = ColorFilter.tint(
                            Yellow70
                        )
                    )

                    Text(
                        text = "${repository.star}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically)
                    )

                    if (!repository.language.isNullOrEmpty()) {
                        Spacer(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(12.dp)
                                .background(
                                    repository.language
                                        .languageToColor()
                                        .toColor(), CircleShape
                                )
                                .align(Alignment.CenterVertically)
                        )

                        Text(
                            text = repository.language,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }

                Button(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onStarClick(repository) }) {
                    if (repository.repoStarState is RepoStarState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    } else if (repository.repoStarState is RepoStarState.Success) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_star_24),
                            contentDescription = "star",
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(
                                if (repository.repoStarState.starred) Yellow70 else Black80
                            )
                        )
                        Text(
                            text = if (repository.repoStarState.starred) "Unstar" else "Star",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun RepoItemPreview() {
    GitposeTheme {
        RepoItem(
            modifier = Modifier.fillMaxWidth(),
            repository = RepoItem(
                "a/a",
                "dfad",
                "daniel",
                "this is decription",
                13,
                "Java",
                20,
                RepoStarState.Success(true),
                null,
                null
            ),
            onStarClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun TrendingPreviewScreen() {
    GitposeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

        }
    }
}

