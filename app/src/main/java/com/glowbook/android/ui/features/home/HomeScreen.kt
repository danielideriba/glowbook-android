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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.glowbook.android.domain.entity.ProfessionalEntity
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
import com.glowbook.android.ui.features.home.model.HomeSampleData
import com.glowbook.android.ui.features.home.model.NextAppointmentUi
import com.glowbook.android.ui.features.home.model.ProfessionalUi
import com.glowbook.android.ui.features.home.model.toUi
import com.glowbook.android.ui.viewmodel.HomeScreenViewModel
import com.glowbook.android.utils.UiState

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val professionalsState by viewModel.professionalsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProfessionalAvailable()
    }

    HomeContent(
        userName = "Ana",
        nextAppointment = HomeSampleData.nextAppointment,
        categories = HomeSampleData.categories,
        professionalsState = professionalsState.toProfessionalsUi(),
        onNextAppointmentClick = {},
        onProfessionalClick = {},
        onBottomNavClick = {}
    )
}

@Composable
fun HomeContent(
    userName: String,
    nextAppointment: NextAppointmentUi?,
    categories: List<CategoryUi>,
    professionalsState: UiState<List<ProfessionalUi>>,
    onNextAppointmentClick: () -> Unit,
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
                    nextAppointment = nextAppointment,
                    onNextAppointmentClick = onNextAppointmentClick
                )
            }

            item {
                Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
                    SectionLabel(text = "Serviços")
                    CategoriesRow(
                        categories = categories,
                        selectedIndex = selectedCategory,
                        onCategoryClick = { selectedCategory = it }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionLabel(text = "Profissionais disponíveis")
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GlowRose)
                    }
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

private fun UiState<List<ProfessionalEntity>>.toProfessionalsUi(): UiState<List<ProfessionalUi>> =
    when (this) {
        is UiState.Success -> UiState.Success(data.map { it.toUi() })
        is UiState.Error -> this
        UiState.Loading -> UiState.Loading
        UiState.Ready -> UiState.Ready
    }

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeContentPreview() {
    HomeContent(
        userName = "Ana",
        nextAppointment = HomeSampleData.nextAppointment,
        categories = HomeSampleData.categories,
        professionalsState = UiState.Success(HomeSampleData.professionals),
        onNextAppointmentClick = {},
        onProfessionalClick = {},
        onBottomNavClick = {}
    )
}
