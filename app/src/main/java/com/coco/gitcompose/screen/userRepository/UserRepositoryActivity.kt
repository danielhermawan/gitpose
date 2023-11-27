package com.coco.gitcompose.screen.userRepository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.GitposeSnackbarHost
import com.coco.gitcompose.core.ui.handleSnackbarState
import com.coco.gitcompose.core.ui.theme.GitposeTheme
import com.coco.gitcompose.core.ui.theme.Pink40
import com.coco.gitcompose.core.ui.theme.Yellow70
import com.coco.gitcompose.core.util.toColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserRepositoryActivity : ComponentActivity() {
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, UserRepositoryActivity::class.java)
        }
    }

    private val viewModel: UserRepositoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GitposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    val snackbarHostState = remember {
                        SnackbarHostState()
                    }

                    UserRepositoryScreen(
                        uiState = uiState,
                        snackbarHostState = snackbarHostState,
                        screenListener = object : ScreenListener {
                            override fun onBackPressed() {
                                finish()
                            }

                            override fun onFilterTypeSelected(repoTypeLabel: RepoTypeLabel) {
                                viewModel.filterTypeSelected(repoTypeLabel)
                            }

                            override fun onFilterSortSelected(sortLabel: SortLabel) {
                                viewModel.filterSortSelected(sortLabel)
                            }

                            override fun onResetFilterClick() {
                                viewModel.resetFilter()
                            }

                            override fun onPullToRefresh() {
                                viewModel.onPullToRefresh()
                            }

                            override fun onReloadPage() {
                                viewModel.reloadPage()
                            }

                            override fun onLoadNextPage() {
                                viewModel.loadNextPage()
                            }

                            override fun onRepositoryClick() {
                                //todo: go to repository detail
                            }
                        }
                    )

                    snackbarHostState.handleSnackbarState(
                        snackbarStateState = uiState.snackbarState,
                        onSnackbarMessageShown = { viewModel.onSnackbarMessageShown() }
                    )
                }
            }
        }
    }
}

@Composable
fun UserRepositoryScreen(
    modifier: Modifier = Modifier,
    uiState: UserRepositoryUiState = UserRepositoryUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    screenListener: ScreenListener
) {
    Scaffold(
        snackbarHost = {
            GitposeSnackbarHost(
                hostState = snackbarHostState
            )
        },
        topBar = {
            UserRepositoryAppBar(
                ownerName = uiState.loginName
            ) { screenListener.onBackPressed() }
        },
        modifier = modifier
    ) { padding ->
        UserRepositoryContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            uiState = uiState,
            screenListener = screenListener
        )
    }
}

@Composable
fun UserRepositoryContent(
    modifier: Modifier = Modifier,
    uiState: UserRepositoryUiState = UserRepositoryUiState(),
    screenListener: ScreenListener
) {
    var showTopElevation by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        FilterBar(
            modifier = Modifier.fillMaxWidth(),
            showTopElevation = showTopElevation,
            selectedType = uiState.selectedRepoType,
            selectedSortLabel = uiState.selectedSortOption,
            listSortOptions = uiState.listSortOptions,
            listRepoTypes = uiState.listRepoTypes,
            filterCount = uiState.filterCount,
            onFilterTypeSelected = { repoTypeLabel ->
                screenListener.onFilterTypeSelected(repoTypeLabel)
            },
            onFilterSortSelected = { sortLabel ->
                screenListener.onFilterSortSelected(sortLabel)
            },
            onResetFilterClick = {
                screenListener.onResetFilterClick()
            }
        )
        RepositoriesSection(
            modifier = Modifier.fillMaxSize(),
            ownerRepoUiState = uiState.ownerRepoUiState,
            showPullToRefreshLoading = uiState.isPullToRefresh,
            showLoadingNextPage = uiState.loadingNextPage,
            loadNextPageOnProgress = uiState.loadNextPageOnProgress,
            onRepositoryClick = {
                screenListener.onRepositoryClick()
            },
            onPullToRefresh = {
                screenListener.onPullToRefresh()
            },
            onResetFilterClick = {
                screenListener.onResetFilterClick()
            },
            onReloadPage = {
                screenListener.onReloadPage()
            },
            onScrollPassFirstItem = { zeroOffset ->
                showTopElevation = zeroOffset
            },
            onLoadNextPage = {
                screenListener.onLoadNextPage()
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RepositoriesSection(
    modifier: Modifier = Modifier,
    ownerRepoUiState: OwnerRepoUiState,
    showPullToRefreshLoading: Boolean,
    showLoadingNextPage: Boolean,
    loadNextPageOnProgress: Boolean,
    onRepositoryClick: (OwnerRepoViewModel) -> Unit,
    onPullToRefresh: () -> Unit,
    onResetFilterClick: () -> Unit,
    onReloadPage: () -> Unit,
    onScrollPassFirstItem: (Boolean) -> Unit,
    onLoadNextPage: () -> Unit
) {
    val pullRefreshState =
        rememberPullRefreshState(showPullToRefreshLoading, { onPullToRefresh() })
    val enabledPullToRefresh =
        !showPullToRefreshLoading || ownerRepoUiState !is OwnerRepoUiState.Loading

    var boxModifier = modifier
        .pullRefresh(
            pullRefreshState,
            enabled = enabledPullToRefresh
        )
    if (ownerRepoUiState !is OwnerRepoUiState.Success) {
        boxModifier = boxModifier.verticalScroll(rememberScrollState())
    }
    Box(
        modifier = boxModifier
    ) {
        when (ownerRepoUiState) {
            is OwnerRepoUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            is OwnerRepoUiState.Error -> {
                ErrorSection(
                    modifier = Modifier.align(Alignment.Center),
                    errorMessage = ownerRepoUiState.messageError,
                    onTryAgainClick = onReloadPage
                )
            }

            is OwnerRepoUiState.Empty -> {
                EmptySection(
                    modifier = Modifier.align(Alignment.Center),
                    showResetFilter = ownerRepoUiState.showResetFilter,
                    onResetFilterClick = onResetFilterClick
                )
            }

            is OwnerRepoUiState.Success -> {
                RepoListSection(
                    recentRepos = ownerRepoUiState.recentRepos,
                    pullRefreshState = pullRefreshState,
                    enabledPullToRefresh = enabledPullToRefresh,
                    showLoadingNextPage = showLoadingNextPage,
                    loadNextPageOnProgress = loadNextPageOnProgress,
                    onRepoClick = { onRepositoryClick(it) },
                    onScrollFirstItem = onScrollPassFirstItem,
                    onLoadNextPage = onLoadNextPage
                )
            }
        }

        PullRefreshIndicator(
            refreshing = showPullToRefreshLoading,
            state = pullRefreshState,
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RepoListSection(
    modifier: Modifier = Modifier,
    recentRepos: List<OwnerRepoViewModel>,
    pullRefreshState: PullRefreshState,
    enabledPullToRefresh: Boolean,
    showLoadingNextPage: Boolean,
    loadNextPageOnProgress: Boolean,
    onRepoClick: (OwnerRepoViewModel) -> Unit,
    onScrollFirstItem: (Boolean) -> Unit,
    onLoadNextPage: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .map { index -> index == 0 }
            .distinctUntilChanged()
            .collect { zeroOffset ->
                onScrollFirstItem(!zeroOffset)
            }
    }

    if (showLoadingNextPage && !loadNextPageOnProgress) {
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo }
                .map { layoutInfo ->
                    (layoutInfo.visibleItemsInfo.lastOrNull()?.index
                        ?: 0) >= layoutInfo.totalItemsCount - 2
                }
                .distinctUntilChanged()
                .collect { loadMore ->
                    if (loadMore) {
                        onLoadNextPage()
                    }
                }
        }

    }

    LazyColumn(
        modifier = modifier
            .pullRefresh(pullRefreshState, enabled = enabledPullToRefresh),
        state = listState
    ) {
        items(items = recentRepos, key = { repo ->
            repo.id
        }) { repo ->
            RepoItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onRepoClick(repo) },
                repository = repo
            )
        }

        if (showLoadingNextPage) {
            item {
                Box(
                    modifier = modifier
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
        }

    }

}

@Composable
fun RepoItem(
    modifier: Modifier = Modifier,
    repository: OwnerRepoViewModel
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_lock_24), contentDescription = null)

            Text(
                repository.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (!repository.description.isNullOrEmpty()) {
            Text(
                repository.description,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (!repository.forkedFrom.isNullOrEmpty()) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fork_24),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    "Forked from ${repository.forkedFrom}",
                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_star_filled),
                contentDescription = "Star Icon",
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(Yellow70)
            )

            Text(
                text = "${repository.starCount}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            )

            if (!repository.language.isNullOrEmpty()) {
                Spacer(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(12.dp)
                        .background(repository.color?.toColor() ?: Pink40, CircleShape)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = repository.language,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        Spacer(modifier = Modifier.padding(top = 16.dp))

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun ErrorSection(
    modifier: Modifier = Modifier,
    @StringRes errorMessage: Int?,
    onTryAgainClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = errorMessage ?: R.string.common_server_error),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Button(
            shape = MaterialTheme.shapes.small,
            onClick = onTryAgainClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.user_repository_error_try_again),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun EmptySection(
    modifier: Modifier = Modifier,
    showResetFilter: Boolean,
    onResetFilterClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.user_repository_empty_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (showResetFilter) {
            Text(
                text = stringResource(R.string.user_repository_empty_section_reset_filter_description),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            Button(
                shape = MaterialTheme.shapes.small,
                onClick = { onResetFilterClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.user_repositories_reset_all_filter),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}


@Composable
fun FilterBar(
    modifier: Modifier = Modifier,
    showTopElevation: Boolean = false,
    selectedType: RepoTypeLabel,
    selectedSortLabel: SortLabel,
    listSortOptions: List<SortLabel>,
    listRepoTypes: List<RepoTypeLabel>,
    filterCount: Int = 0,
    onFilterTypeSelected: (RepoTypeLabel) -> Unit = {},
    onFilterSortSelected: (SortLabel) -> Unit = {},
    onResetFilterClick: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier,
        shadowElevation = if (showTopElevation) 4.dp else 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)
            ) {
                ResetFilterButton(
                    filterCount = filterCount,
                    onResetFilterClick = onResetFilterClick
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    FilterType(
                        selectedType = selectedType,
                        listRepoTypes = listRepoTypes,
                        onFilterTypeSelected = onFilterTypeSelected
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterSort(
                        selectedSortLabel = selectedSortLabel,
                        listSortOptions = listSortOptions,
                        onFilterSortSelected = onFilterSortSelected
                    )

                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
fun ResetFilterButton(
    modifier: Modifier = Modifier,
    filterCount: Int = 0,
    onResetFilterClick: () -> Unit = {}
) {
    var showResetFilterMenu by remember {
        mutableStateOf(false)
    }
    if (filterCount > 0) {
        Box(
            modifier = modifier
        ) {
            FilterButton(count = filterCount, icon = R.drawable.ic_filter_24, onClick = {
                showResetFilterMenu = true
            })
            DropdownMenu(
                expanded = showResetFilterMenu,
                offset = DpOffset(x = 0.dp, y = 6.dp),
                onDismissRequest = { showResetFilterMenu = false }) {
                DropdownMenuItem(onClick = {
                    showResetFilterMenu = false
                    onResetFilterClick()
                }) {
                    Text(stringResource(R.string.user_repository_clear_all_filter))
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun FilterType(
    modifier: Modifier = Modifier,
    selectedType: RepoTypeLabel,
    listRepoTypes: List<RepoTypeLabel>,
    onFilterTypeSelected: (RepoTypeLabel) -> Unit = {}
) {
    var showMenuFilterType by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier) {
        FilterButton(
            name = stringResource(selectedType.label),
            selected = !selectedType.default,
            onClick = {
                showMenuFilterType = true
            }
        )
        DropdownMenu(
            expanded = showMenuFilterType,
            offset = DpOffset(x = 0.dp, y = 6.dp),
            onDismissRequest = { showMenuFilterType = false }) {
            for (type in listRepoTypes) {
                DropdownMenuItem(onClick = {
                    showMenuFilterType = false
                    onFilterTypeSelected(type)
                }) {
                    Text(stringResource(type.label), modifier = Modifier.weight(1f))
                    if (type.selected) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_check_24),
                            contentDescription = "Icon ${type.label}",
                            modifier = Modifier
                                .size(24.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSort(
    modifier: Modifier = Modifier,
    selectedSortLabel: SortLabel,
    listSortOptions: List<SortLabel>,
    onFilterSortSelected: (SortLabel) -> Unit = {}
) {
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    FilterButton(
        name = stringResource(R.string.user_repository_label_sort)
                + stringResource(selectedSortLabel.label),
        modifier = modifier,
        selected = !selectedSortLabel.default,
        onClick = {
            showBottomSheet = true
        }
    )
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        scope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                    }
                    .padding(start = 20.dp, top = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_24),
                    contentDescription = null
                )
                Text(
                    text = "Sort by",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(top = 16.dp),
                color = MaterialTheme.colorScheme.background
            )

            Column(Modifier.selectableGroup()) {
                listSortOptions.forEach { sortLabel ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = sortLabel.selected,
                                onClick = {
                                    onFilterSortSelected(sortLabel)
                                    scope
                                        .launch { sheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = sortLabel.label),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        RadioButton(
                            selected = sortLabel.selected,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            onClick = null
                        )
                    }

                    if (sortLabel.divider) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp),
                            color = Color.Gray
                        )
                        Spacer(
                            modifier = Modifier
                                .height(16.dp)
                                .background(MaterialTheme.colorScheme.background)
                                .fillMaxWidth()
                        )
                    }

                }
            }

        }
    }
}

@Composable
fun FilterButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.ic_expand_24,
    name: String? = null,
    count: Int? = null,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.secondaryContainer
    else MaterialTheme.colorScheme.background
    val contentColor = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
    else MaterialTheme.colorScheme.onPrimaryContainer
    val iconTintColorColor = if (!name.isNullOrEmpty()) contentColor
    else MaterialTheme.colorScheme.onSecondaryContainer

    Row(
        modifier
            .background(backgroundColor, ShapeDefaults.ExtraLarge)
            .clip(ShapeDefaults.ExtraLarge)
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!name.isNullOrEmpty()) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor
            )
        }
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Filter $name",
            modifier = Modifier
                .size(20.dp)
                .padding(start = 4.dp),
            colorFilter = ColorFilter.tint(iconTintColorColor)
        )

        if (count != null) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(16.dp)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer, CircleShape)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRepositoryAppBar(
    ownerName: String = "",
    onBackPressed: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = ownerName,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Repositories",
                    style = MaterialTheme.typography.titleMedium
                )
            }

        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_24),
                    contentDescription = "Back Action",
                )
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_24),
                    contentDescription = "Search Action",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        },
        modifier = Modifier
    )
}

@Preview(showBackground = true)
@Composable
fun UserRepositoryScreen() {
    GitposeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            UserRepositoryScreen(
                uiState = UserRepositoryUiState(
                    loginName = "danielhermawan",

                    ),
                screenListener = object : ScreenListener {}
            )
        }
    }
}