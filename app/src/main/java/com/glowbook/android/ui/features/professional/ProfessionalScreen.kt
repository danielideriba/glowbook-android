package com.glowbook.android.ui.features.professional

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.glowbook.android.ui.features.home.model.ProfessionalUi
import com.glowbook.android.ui.features.home.model.toUi
import com.glowbook.android.ui.features.professional.components.ProfessionalServicesHeader
import com.glowbook.android.ui.features.professional.components.SelectAllServicesChip
import com.glowbook.android.ui.features.professional.components.ServiceItemCard
import com.glowbook.android.ui.features.professional.components.ServicesTotalFooter
import com.glowbook.android.ui.features.professional.model.ServiceUi
import com.glowbook.android.ui.features.professional.model.summaryFor
import com.glowbook.android.ui.features.professional.model.toUi
import com.glowbook.android.ui.nav.AppScreen
import com.glowbook.android.ui.viewmodel.ProfessionalServicesViewModel
import com.glowbook.android.utils.UiState

@Composable
fun ProfessionalScreen(
    navController: NavController,
    viewModel: ProfessionalServicesViewModel = hiltViewModel()
) {
    val professionalServicesState by viewModel.professionalServicesState.collectAsState()
    val selectedServiceIds by viewModel.selectedServiceIds.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProfessionalServices()
    }

    val success = professionalServicesState as? UiState.Success

    ProfessionalContent(
        professional = success?.data?.professional?.toUi(),
        servicesState = professionalServicesState.mapSuccess { data -> data.services.map { it.toUi() } },
        selectedServiceIds = selectedServiceIds,
        onBackClick = { navController.popBackStack() },
        onServiceClick = { viewModel.toggleService(it.id) },
        onSelectAllClick = { viewModel.toggleAllServices() },
        onContinueClick = {
            success?.data?.professional?.let { professional ->
                navController.navigate(AppScreen.Calendar.createRoute(professional.id, selectedServiceIds))
            }
        },
        onBottomNavClick = { item ->
            when (item) {
                BottomNavItem.Home -> navController.popBackStack(AppScreen.Home.route, inclusive = false)
                BottomNavItem.Schedule -> navController.navigate(AppScreen.Appointments.route)
                else -> Unit
            }
        }
    )
}

@Composable
fun ProfessionalContent(
    professional: ProfessionalUi?,
    servicesState: UiState<List<ServiceUi>>,
    selectedServiceIds: Set<Long>,
    onBackClick: () -> Unit,
    onServiceClick: (ServiceUi) -> Unit,
    onSelectAllClick: () -> Unit,
    onContinueClick: () -> Unit,
    onBottomNavClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val services = (servicesState as? UiState.Success)?.data.orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GlowBackground)
    ) {
        ProfessionalServicesHeader(
            professional = professional,
            onBackClick = onBackClick
        )

        Box(modifier = Modifier.weight(1f)) {
            when (servicesState) {
                is UiState.Success -> ServicesList(
                    services = servicesState.data,
                    selectedServiceIds = selectedServiceIds,
                    onServiceClick = onServiceClick,
                    onSelectAllClick = onSelectAllClick
                )

                is UiState.Error -> ProfessionalMessage(text = servicesState.message)

                UiState.Loading, UiState.Ready -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GlowRose)
                }
            }
        }

        ServicesTotalFooter(
            summary = services.summaryFor(selectedServiceIds),
            onContinueClick = onContinueClick
        )

        GlowBottomNavBar(
            selected = BottomNavItem.Schedule,
            onItemClick = onBottomNavClick
        )
    }
}

@Composable
private fun ServicesList(
    services: List<ServiceUi>,
    selectedServiceIds: Set<Long>,
    onServiceClick: (ServiceUi) -> Unit,
    onSelectAllClick: () -> Unit
) {
    if (services.isEmpty()) {
        ProfessionalMessage(text = stringResource(R.string.error_message_empty))
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                SectionLabel(text = stringResource(R.string.professional_section_choose_services))
                SelectAllServicesChip(
                    allSelected = services.all { it.id in selectedServiceIds },
                    onClick = onSelectAllClick
                )
            }
        }
        items(services, key = { it.id }) { service ->
            ServiceItemCard(
                service = service,
                selected = service.id in selectedServiceIds,
                onClick = { onServiceClick(service) }
            )
        }
    }
}

@Composable
private fun ProfessionalMessage(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        fontSize = 13.sp,
        color = GlowMuted,
        textAlign = TextAlign.Center
    )
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
private fun ProfessionalContentPreview() {
    ProfessionalContent(
        professional = ProfessionalUi(1, "👩", Color(0xFFFDEBD0), "Camila Rocha", "Coloração", 4.9, R.string.professional_available_today),
        servicesState = UiState.Success(
            listOf(
                ServiceUi(1, "✂️", "Corte feminino", "⏱ 45 min", "R$ 80", 80.0, 45),
                ServiceUi(2, "💨", "Escova progressiva", "⏱ 90 min", "R$ 180", 180.0, 90),
                ServiceUi(3, "🎨", "Coloração raiz", "⏱ 60 min", "R$ 120", 120.0, 60)
            )
        ),
        selectedServiceIds = setOf(1, 2),
        onBackClick = {},
        onServiceClick = {},
        onSelectAllClick = {},
        onContinueClick = {},
        onBottomNavClick = {}
    )
}
