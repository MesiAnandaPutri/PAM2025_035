package com.example.projectakhir.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectakhir.R
import com.example.projectakhir.ui.theme.ProjectAkhirTheme
import com.example.projectakhir.uicontroller.route.DestinasiHome
import com.example.projectakhir.uicontroller.route.DestinasiKelolaProduk
import com.example.projectakhir.uicontroller.route.DestinasiLaporan
import com.example.projectakhir.uicontroller.route.DestinasiProfile
import com.example.projectakhir.uicontroller.route.DestinasiTransaksi
import com.example.projectakhir.viewmodel.HomeViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel

val limeColor = Color(0xFFD8FF00)

@Composable
fun HalamanHome(
    onKelolaProdukClicked: () -> Unit,
    onTransaksiClicked: () -> Unit,
    onLaporanClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val state = homeViewModel.homeUIState
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                homeViewModel.getProduk()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(16.dp)
    ) {
        HeaderSection(username = state.username)
        Spacer(modifier = Modifier.height(24.dp))

        // Info Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoCard(label = "Produk", value = state.listProduk.size.toString())
            InfoCard(label = "Transaksi", value = state.jumlahTransaksi.toString())

            val displayPendapatan = if (state.totalPendapatan >= 1_000_000) "${state.totalPendapatan / 1_000_000}M"
            else if (state.totalPendapatan >= 1_000) "${state.totalPendapatan / 1_000}K"
            else state.totalPendapatan.toString()

            InfoCard(label = "Pendapatan", value = displayPendapatan)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        KelolaProdukCard(
            onClick = {
                if (state.role == "Admin") {
                    onKelolaProdukClicked()
                } else {
                    Toast.makeText(
                        context,
                        "Akses Ditolak: Hanya Admin yang dapat mengelola produk",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionCard(
                label = "Transaksi",
                imageResId = R.drawable.transaksi,
                modifier = Modifier.clickable { onTransaksiClicked() }
            )
            ActionCard(
                label = "Laporan",
                imageResId = R.drawable.laporan,
                modifier = Modifier
                    .clickable {
                        if (state.role == "Admin") {
                            onLaporanClicked()
                        } else {
                            // Jika Staff, tampilkan pesan peringatan dan tidak pindah halaman
                            Toast.makeText(
                                context,
                                "Akses Ditolak: Hanya Admin yang dapat melihat laporan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .then(
                        // Memberikan efek visual sedikit pudar jika bukan admin agar terlihat "disabled"
                        if (state.role != "Admin") Modifier.alpha(0.5f) else Modifier
                    )
            )
            ActionCard(
                label = "Profile",
                imageResId = R.drawable.profile,
                modifier = Modifier.clickable { onProfileClicked() }
            )
        }
    }
}


@Composable
fun AppBottomNavigationBar(
    currentRoute: String?,
    onNavigateToHome: () -> Unit,
    onNavigateToLaporan: () -> Unit,
    onNavigateToKelola: () -> Unit,
    onNavigateToTransaksi: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = currentRoute == DestinasiHome.route,
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(indicatorColor = limeColor)
        )
        NavigationBarItem(
            selected = currentRoute == DestinasiLaporan.route,
            onClick = onNavigateToLaporan,
            icon = { Image(painterResource(R.drawable.laporan), null, Modifier.size(24.dp)) },
            label = { Text("Laporan") }
        )
        NavigationBarItem(
            selected = currentRoute == DestinasiKelolaProduk.route,
            onClick = onNavigateToKelola,
            icon = { Image(painterResource(R.drawable.kelola), null, Modifier.size(24.dp)) },
            label = { Text("Kelola") }
        )
        NavigationBarItem(
            selected = currentRoute == DestinasiTransaksi.route,
            onClick = onNavigateToTransaksi,
            icon = { Image(painterResource(R.drawable.transaksi), null, Modifier.size(24.dp)) },
            label = { Text("Transaksi") }
        )
        NavigationBarItem(
            selected = currentRoute == DestinasiProfile.route,
            onClick = onNavigateToProfile,
            icon = { Image(painterResource(R.drawable.profile), null, Modifier.size(24.dp)) },
            label = { Text("Profile") }
        )
    }
}



@Composable
fun HeaderSection(username: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "Profile Icon",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Halo,", fontSize = 16.sp, color = Color.Gray)
            Text(username, fontSize = 20.sp, fontWeight = FontWeight.Bold) // Gunakan parameter
        }
    }
}

@Composable
fun RowScope.InfoCard(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun KelolaProdukCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = limeColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kelola),
                        contentDescription = "Produk Icon",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.lanjut),
                    contentDescription = "Lanjut",
                    modifier = Modifier.size(24.dp) // Sesuaikan ukurannya jika perlu
                )            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Kelola Produk", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Atur stok & harga", fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun RowScope.ActionCard(label: String, imageResId: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .weight(1f)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painterResource(imageResId), null, Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 12.sp)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHalamanHome() {
    ProjectAkhirTheme {
        HalamanHome(
            onKelolaProdukClicked = {},
            onTransaksiClicked = {},
            onLaporanClicked = {},
            onProfileClicked = {}
        )
    }
}
