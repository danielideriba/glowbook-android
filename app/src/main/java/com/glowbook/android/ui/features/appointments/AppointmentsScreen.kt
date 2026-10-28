package com.glowbook.android.ui.features.appointments

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.glowbook.android.ui.common.theme.GlowBackground
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowRose
import com.glowbook.android.ui.components.BottomNavItem
import com.glowbook.android.ui.components.GlowBottomNavBar
import com.glowbook.android.ui.features.appointments.components.AppointmentCard
import com.glowbook.android.ui.features.appointments.components.AppointmentsHeader
import com.glowbook.android.ui.features.appointments.model.AppointmentStatusUi
import com.glowbook.android.ui.features.appointments.model.AppointmentUi
import com.glowbook.android.ui.features.appointments.model.AppointmentsTab
import com.glowbook.android.ui.features.appointments.model.toUi
import com.glowbook.android.ui.nav.AppScreen
import com.glowbook.android.ui.viewmodel.AppointmentsViewModel
import com.glowbook.android.utils.UiState
import com.glowbook.android.R

@Composable
fun AppointmentsScreen(
    navController: NavController,
    viewModel: AppointmentsViewModel = hiltViewModel()
) {
    val appointmentsUpNextState by viewModel.appointmentsUpNextState.collectAsState()
    val appointmentsHistoryState by viewModel.appointmentsHistoryState.collectAsState()
    val cancelledState by viewModel.cancelledState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAppointmentsUpNext()
        viewModel.fetchAppointmentsHistory()
        viewModel.fetchAppointmentsCancelled()
    }

    AppointmentsContent(
        appointmentsUpNextState = appointmentsUpNextState.mapSuccess { appointments -> appointments.map { it.toUi() } },
        appointmentsHistoryState = appointmentsHistoryState.mapSuccess { appointments -> appointments.map { it.toUi() } },
        cancelledState = cancelledState.mapSuccess { appointments -> appointments.map { it.toUi() } },
        onBackClick = { navController.popBackStack() },
        onBottomNavClick = { item ->
            if (item == BottomNavItem.Home) {
                navController.popBackStack(AppScreen.Home.route, inclusive = false)
            }
        }
    )
}

@Composable
fun AppointmentsContent(
    appointmentsUpNextState: UiState<List<AppointmentUi>>,
    appointmentsHistoryState: UiState<List<AppointmentUi>>,
    cancelledState: UiState<List<AppointmentUi>>,
    onBackClick: () -> Unit,
    onBottomNavClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(AppointmentsTab.Upcoming) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GlowBackground)
    ) {
        AppointmentsHeader(
            selectedTab = selectedTab,
            onTabClick = { selectedTab = it },
            onBackClick = onBackClick
        )

        val tabState = when (selectedTab) {
            AppointmentsTab.Upcoming -> appointmentsUpNextState
            AppointmentsTab.History -> appointmentsHistoryState
            AppointmentsTab.Cancelled -> cancelledState
        }

        Box(modifier = Modifier.weight(1f)) {
            when (tabState) {
                is UiState.Success -> AppointmentsList(
                    appointments = tabState.data,
                    emptyMessage = stringResource(emptyMessageFor(selectedTab))
                )

                is UiState.Error -> AppointmentsMessage(text = tabState.message)

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

        GlowBottomNavBar(
            selected = BottomNavItem.Schedule,
            onItemClick = onBottomNavClick
        )
    }
}

@Composable
private fun AppointmentsList(
    appointments: List<AppointmentUi>,
    emptyMessage: String
) {
    if (appointments.isEmpty()) {
        AppointmentsMessage(text = emptyMessage)
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(appointments, key = { it.id }) { appointment ->
            AppointmentCard(
                appointment = appointment,
                onClick = {},
                onDetailsClick = {},
                onRescheduleClick = {},
                onCancelClick = {},
                onRateClick = {}
            )
        }
    }
}

@Composable
private fun AppointmentsMessage(text: String) {
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

@StringRes
private fun emptyMessageFor(tab: AppointmentsTab): Int = when (tab) {
    AppointmentsTab.Upcoming -> R.string.error_message_empty_upcoming
    AppointmentsTab.History -> R.string.error_message_empty_history
    AppointmentsTab.Cancelled -> R.string.error_message_empty_cancelled
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
private fun AppointmentsContentPreview() {
    AppointmentsContent(
        appointmentsUpNextState = UiState.Success(
            listOf(
                AppointmentUi(1, "Corte + Escova", "👩 Ka Rocha", AppointmentStatusUi.Confirmed, "Qui, 26 Jun", "14:00", "R$ 260", AppointmentsTab.Upcoming),
                AppointmentUi(2, "Nail Art completo", "🧑 Be Lima", AppointmentStatusUi.Pending, "Sáb, 28 Jun", "10:00", "R$ 120", AppointmentsTab.Upcoming)
            )
        ),
        appointmentsHistoryState = UiState.Success(emptyList()),
        cancelledState = UiState.Success(emptyList()),
        onBackClick = {},
        onBottomNavClick = {}
    )
}
