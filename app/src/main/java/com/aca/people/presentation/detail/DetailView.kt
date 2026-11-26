package com.aca.people.presentation.detail // El package sigue igual

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// import androidx.navigation.NavHostController // <<--- CAMBIO: Ya no se necesita
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.aca.people.R
import com.aca.people.domain.User
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(
    viewModel: DetailViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when(val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is DetailUiState.NotFound -> {
                    Text("Usuario no encontrado")
                }
                is DetailUiState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                is DetailUiState.Success -> {
                    DetailContent(state.user)
                }
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
            Text(
                text = "${name.title.orEmpty()} ${name.first.orEmpty()} ${name.last.orEmpty()}".trim(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        user.email?.let { email ->
            InfoRow(
                icon = R.drawable.ic_email,
                tint = Color.Blue,
                text = email
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        user.phone?.let { phone ->
            InfoRow(
                icon = R.drawable.ic_phone,
                tint = Color.Green,
                text = phone
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        user.gender?.let { gender ->
            InfoRow(
                icon = R.drawable.ic_person,
                tint = Color.Magenta,
                text = "Gender: ${gender.replaceFirstChar { it.titlecase(Locale.getDefault()) }}"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        user.location?.let { location ->
            Text("Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(
                icon = R.drawable.ic_location,
                tint = Color.Red,
                text = "${location.city.orEmpty()}, ${location.state.orEmpty()}, ${location.country.orEmpty()}".trim()
            )
            Spacer(modifier = Modifier.height(4.dp))
            InfoRow(
                icon = R.drawable.ic_globe,
                tint = Color.Blue,
                text = "Coordinates: ${location.coordinates?.latitude ?: "N/A"}, ${location.coordinates?.longitude ?: "N/A"}"
            )
            Spacer(modifier = Modifier.height(4.dp))
            InfoRow(
                icon = R.drawable.ic_clock,
                tint = Color.DarkGray,
                text = "Timezone: ${location.timezone?.description.orEmpty()} (${location.timezone?.offset.orEmpty()})".trim()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// CAMBIO EXTRA: He creado un Composable reutilizable para las filas de información.
// Esto reduce la duplicación de código y hace que el `Column` principal sea más fácil de leer.
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
        Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
