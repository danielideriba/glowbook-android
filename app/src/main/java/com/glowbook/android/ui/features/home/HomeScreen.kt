package com.glowbook.android.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.glowbook.android.R
import com.glowbook.android.ui.common.theme.GlowBackground
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowRose
import com.glowbook.android.ui.components.BottomNavItem
import com.glowbook.android.ui.components.GlowBottomNavBar
import com.glowbook.android.ui.components.SectionLabel
import com.glowbook.android.ui.features.home.components.CategoriesRow
import com.glowbook.android.ui.features.home.components.HomeHeader
import com.glowbook.android.ui.features.home.components.ProfessionalCard
import com.glowbook.android.ui.features.home.model.CategoryUi
import com.glowbook.android.ui.features.home.model.NextAppointmentUi
import com.glowbook.android.ui.features.home.model.ProfessionalUi
import com.glowbook.android.ui.features.home.model.toUi
import com.glowbook.android.ui.nav.AppScreen
import com.glowbook.android.ui.viewmodel.HomeScreenViewModel
import com.glowbook.android.utils.UiState

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val professionalsState by viewModel.professionalsState.collectAsState()
    val categoriesState by viewModel.categoriesState.collectAsState()
    val nextAppointmentState by viewModel.nextAppointmentState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchHomeData()
    }

    HomeContent(
        userName = stringResource(R.string.home_default_username),
        nextAppointments = (nextAppointmentState as? UiState.Success)?.data?.map { it.toUi() }.orEmpty(),
        categoriesState = categoriesState.mapSuccess { categories -> categories.map { it.toUi() } },
        professionalsState = professionalsState.mapSuccess { professionals -> professionals.map { it.toUi() } },
        onNextAppointmentClick = { navController.navigate(AppScreen.Appointments.route) },
        onProfessionalClick = { professional ->
            navController.navigate(AppScreen.ProfessionalServices.createRoute(professional.id))
        },
        onBottomNavClick = { item ->
            if (item == BottomNavItem.Schedule) {
                navController.navigate(AppScreen.Appointments.route)
            }
        }
    )
}

@Composable
fun HomeContent(
    userName: String,
    nextAppointments: List<NextAppointmentUi>,
    categoriesState: UiState<List<CategoryUi>>,
    professionalsState: UiState<List<ProfessionalUi>>,
    onNextAppointmentClick: (NextAppointmentUi) -> Unit,
    onProfessionalClick: (ProfessionalUi) -> Unit,
    onBottomNavClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GlowBackground)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                HomeHeader(
                    userName = userName,
                    nextAppointments = nextAppointments,
                    onNextAppointmentClick = onNextAppointmentClick
                )
            }

            item {
                Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
                    SectionLabel(text = stringResource(R.string.home_section_services))
                    when (categoriesState) {
                        is UiState.Success -> CategoriesRow(
                            categories = categoriesState.data,
                            selectedIndex = selectedCategory,
                            onCategoryClick = { selectedCategory = it }
                        )

                        is UiState.Error -> HomeMessage(text = categoriesState.message)

                        UiState.Loading, UiState.Ready -> HomeLoading()
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionLabel(text = stringResource(R.string.home_section_available_professionals))
                }
            }

            when (professionalsState) {
                is UiState.Success -> items(professionalsState.data) { professional ->
                    ProfessionalCard(
                        professional = professional,
                        onClick = { onProfessionalClick(professional) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                    )
                }

                is UiState.Error -> item {
                    HomeMessage(text = professionalsState.message)
                }

                UiState.Loading, UiState.Ready -> item {
                    HomeLoading()
                }
            }
        }

        GlowBottomNavBar(
            selected = BottomNavItem.Home,
            onItemClick = onBottomNavClick
        )
    }
}

@Composable
private fun HomeMessage(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        fontSize = 13.sp,
        color = GlowMuted
    )
}

@Composable
private fun HomeLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = GlowRose)
    }
}

private fun <T, R> UiState<T>.mapSuccess(transform: (T) -> R): UiState<R> =
    when (this) {
        is UiState.Success -> UiState.Success(transform(data))
        is UiState.Error -> this
        UiState.Loading -> UiState.Loading
        UiState.Ready -> UiState.Ready
    }

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeContentPreview() {
    HomeContent(
        userName = "Ana",
        nextAppointments = listOf(
            NextAppointmentUi(icon = "✂️", title = "Corte + Escova", subtitle = "Sexta, 27 Jun • 14:00 • Camila"),
            NextAppointmentUi(icon = "💅", title = "Nail Art completo", subtitle = "Sábado, 28 Jun • 10:00 • Beatriz")
        ),
        categoriesState = UiState.Success(listOf(CategoryUi("✂️", "Cabelo"), CategoryUi("💅", "Unhas"), CategoryUi("✨", "Estética"), CategoryUi("🧖", "Spa"))),
        professionalsState = UiState.Success(listOf(ProfessionalUi(1, "👩", Color(0xFFFDEBD0), "Camila Rocha", "Cabelo • Coloração", 4.9, R.string.professional_available_today))),
        onNextAppointmentClick = {},
        onProfessionalClick = {},
        onBottomNavClick = {}
    )
}
