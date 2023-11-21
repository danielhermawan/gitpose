package com.coco.gitcompose.screen.userRepository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.GitposeSnackbarHost
import com.coco.gitcompose.core.ui.handleSnackbarState
import com.coco.gitcompose.core.ui.theme.GitposeTheme
import dagger.hilt.android.AndroidEntryPoint

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
                ownerName = uiState.loginName,
                onBackPressed = { screenListener.onBackPressed() }
            )
        },
        modifier = modifier
    ) { padding ->
        UserRepositoryContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            uiState = uiState
        )
    }
}

@Composable
fun UserRepositoryContent(
    modifier: Modifier = Modifier,
    uiState: UserRepositoryUiState = UserRepositoryUiState(),
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
            listRepoTypes = uiState.listRepoTypes
        )
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
    filterCount: Int = 0
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier,
        shadowElevation = if (showTopElevation) 16.dp else 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)
            ) {
                if (filterCount > 0) {
                    FilterButton(count = filterCount, icon = R.drawable.ic_filter_24)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    FilterButton(
                        name = stringResource(selectedType.label),
                        selected = !selectedType.default,
                        onClick = {

                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterButton(
                        name = stringResource(R.string.user_repository_label_sort)
                                + stringResource(selectedSortLabel.label),
                        selected = !selectedSortLabel.default,
                        onClick = {

                        }
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
    modifier: Modifier = Modifier,
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
                    loginName = "danielhermawan"
                ),
                screenListener = object : ScreenListener {
                    override fun onBackPressed() {

                    }

                }
            )
        }
    }
}