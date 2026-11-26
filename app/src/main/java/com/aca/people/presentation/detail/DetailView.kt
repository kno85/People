package com.aca.people.presentation.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.aca.people.R
import com.aca.people.domain.User
import com.aca.people.domain.UserLocation
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(
    viewModel: DetailViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> CircularProgressIndicator()
                is DetailUiState.NotFound -> Text("Usuario no encontrado")
                is DetailUiState.Error -> Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
                is DetailUiState.Success -> DetailContent(state.user)
            }
        }
    }
}

@Composable
fun DetailContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        DetailHeader(user = user)
        DetailContactSection(user = user)
        user.location?.let { location ->
            Spacer(modifier = Modifier.height(16.dp))
            DetailLocationSection(location = location)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DetailHeader(user: User) {
    user.picture?.large?.let { pictureUrl ->
        val painter = rememberAsyncImagePainter(pictureUrl)
        Image(
            painter = painter,
            contentDescription = "Profile picture of ${user.name?.first}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .padding(top = 16.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    user.name?.let { name ->
        val fullName = "${name.title.orEmpty()} ${name.first.orEmpty()} ${name.last.orEmpty()}".trim()
        Text(
            text = fullName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DetailContactSection(user: User) {
    Spacer(modifier = Modifier.height(8.dp))

    user.email?.let { email ->
        InfoRow(icon = R.drawable.ic_email, tint = Color.Blue, text = email)
    }

    Spacer(modifier = Modifier.height(8.dp))

    user.phone?.let { phone ->
        InfoRow(icon = R.drawable.ic_phone, tint = Color.Green, text = phone)
    }

    Spacer(modifier = Modifier.height(8.dp))

    user.gender?.let { gender ->
        val formattedGender = gender.replaceFirstChar { it.titlecase(Locale.getDefault()) }
        InfoRow(icon = R.drawable.ic_person, tint = Color.Magenta, text = "Gender: $formattedGender")
    }
}

@Composable
private fun DetailLocationSection(location: UserLocation) {
    Text(
        text = "Location",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))

    val address = "${location.city.orEmpty()}, ${location.state.orEmpty()}, ${location.country.orEmpty()}".trim()
    InfoRow(icon = R.drawable.ic_location, tint = Color.Red, text = address)

    Spacer(modifier = Modifier.height(4.dp))

    val lat = location.coordinates?.latitude ?: "N/A"
    val lon = location.coordinates?.longitude ?: "N/A"
    InfoRow(icon = R.drawable.ic_globe, tint = Color.Blue, text = "Coordinates: $lat, $lon")

    Spacer(modifier = Modifier.height(4.dp))

    val tzDesc = location.timezone?.description.orEmpty()
    val tzOffset = location.timezone?.offset.orEmpty()
    InfoRow(icon = R.drawable.ic_clock, tint = Color.DarkGray, text = "Timezone: $tzDesc ($tzOffset)".trim())
}

@Composable
private fun InfoRow(icon: Int, tint: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

