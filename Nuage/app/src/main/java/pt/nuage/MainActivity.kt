package pt.nuage

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pt.nuage.services.HomeScreenViewModelFactory
import pt.nuage.services.Settings.saveLocation
import pt.nuage.ui.navigation.NavBarBody
import pt.nuage.ui.navigation.NavBarHeader
import pt.nuage.ui.navigation.NavigationItem
import pt.nuage.ui.navigation.Screens
import pt.nuage.ui.navigation.SetUpNavGraph
import pt.nuage.ui.screens.HomeScreenViewModel
import pt.nuage.ui.theme.NuageTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context: Context = this
            val nuageViewModel: HomeScreenViewModel = viewModel(
                factory = HomeScreenViewModelFactory(this.applicationContext)
            )

            val navControllerApp = rememberNavController()
            NuageTheme {
                val items = listOf(
                    NavigationItem(
                        title = stringResource(R.string.screenHomeName),
                        route = Screens.App.route,
                        selectedIcon = Icons.Filled.Home,
                        unselectedicon = Icons.Outlined.Home,
                    ),
                    NavigationItem(
                        title = stringResource(R.string.screenAboutName),
                        route = Screens.About.route,
                        selectedIcon = Icons.Filled.Info,
                        unselectedicon = Icons.Outlined.Info,
                    ),
                )
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val topBarTitle =
                    if (currentRoute != null) {
                        items[items.indexOfFirst {
                            it.route == currentRoute
                        }].title
                    } else {
                        items[0].title
                    }
                ModalNavigationDrawer(
                    gesturesEnabled = drawerState.isOpen, drawerContent = {
                        ModalDrawerSheet {
                            NavBarHeader()
                            Spacer(modifier = Modifier.height(8.dp))
                            NavBarBody(
                                items = items,
                                currentRoute = currentRoute
                            ) { currentNavigationItem ->
                                navController.navigate(currentNavigationItem.route) {
                                    navController.graph.startDestinationRoute?.let {
                                        popUpTo(it) {
                                            saveState = true
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        }
                    }, drawerState = drawerState
                ) {
                    Scaffold(
                        topBar = {
                            MainAppBar(
                                topBarTitle = topBarTitle,
                                drawerScope = scope,
                                drawerState = drawerState,
                                viewModel = nuageViewModel,
                            )
                        }
                    ) { innerPadding ->
                        SetUpNavGraph(
                            navController = navController,
                            innerPadding = innerPadding,
                            context = this,
                            secondNavGraph = navControllerApp,
                            homeScreenViewModel = nuageViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppBar(
    topBarTitle: String,
    drawerScope: CoroutineScope,
    drawerState: DrawerState,
    viewModel: HomeScreenViewModel
) {
    var isOpened: Boolean by remember {
        mutableStateOf(false)
    }
    when (isOpened) {
        false -> {
            DefaultAppBar(
                topBarTitle = topBarTitle, drawerScope = drawerScope, drawerState = drawerState,
                onSearchClicked = { isOpened = true }
            )
        }

        true -> {
            SearchAppBar(
                onCloseClicked = { isOpened = false },
                viewModel
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(
    topBarTitle: String,
    drawerScope: CoroutineScope,
    drawerState: DrawerState,
    onSearchClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = topBarTitle)
        },
        navigationIcon = {
            IconButton(onClick = {
                drawerScope.launch {
                    drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Icon"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                onSearchClicked()
            }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
    onCloseClicked: () -> Unit,
    viewModel: HomeScreenViewModel,
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchData by viewModel.searchData.collectAsState()
    Box(Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchText,
                    onQueryChange = viewModel::onSearchTextChange,
                    onSearch = { query ->
                        viewModel.getSearchResults(query)
                    },
                    expanded = isSearching,
                    onExpandedChange = { viewModel.onToogleSearch() },
                    placeholder = { Text("Search for a location") },
                    trailingIcon = {
                        IconButton(onClick = { onCloseClicked() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.appBarCloseButton)
                            )
                        }
                    }
                )
            },
            expanded = isSearching,
            onExpandedChange = { viewModel.onToogleSearch() }
        ) {
            if(searchData.isNotEmpty()) {
                LazyColumn ( modifier = Modifier.fillMaxSize().padding(16.dp), // optional
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(searchData.size) {
                        val item = searchData[it]
                        val scope = rememberCoroutineScope()
                        Card(
                            onClick = {
                                scope.launch {
                                    saveLocation(context, item.latitude, item.longitude)
                                    viewModel.refreshWeatherData()
                                }
                                viewModel.onToogleSearch()
                                onCloseClicked()
                            }
                        ) {
                            Column(Modifier.padding(16.dp).fillMaxWidth()) {
                                Text(text = item.admin1, style = TextStyle(fontSize = 24.sp))
                                Text(text =  "${item.name}")
                                Text(text =  "${item.country}")
                                Text(text = "${item.latitude} ${item.longitude}")
                            }
                        }
                    }
                }
            } else if (searchText.isNotBlank() && searchData.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No results found.")
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Search for a city or place...")
                }
            }
        }
    }
}

