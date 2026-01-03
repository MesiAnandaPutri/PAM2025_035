package com.example.projectakhir.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectakhir.R
import com.example.projectakhir.ui.theme.ProjectAkhirTheme
import com.example.projectakhir.viewmodel.HomeViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel

val limeColor = Color(0xFFD8FF00)

@Composable
fun HalamanHome(
    onKelolaProdukClicked: () -> Unit,
    // onNavigateToLaporan: () -> Unit, // Parameter ini belum digunakan, bisa dihapus sementara
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val state = homeViewModel.homeUIState

    Scaffold(
        // --- KESALAHAN #1: onKelolaProdukClicked belum diteruskan ke BottomNavigationBar ---
        // PERBAIKAN: Teruskan callback ke BottomNavigationBar
        bottomBar = { BottomNavigationBar(onKelolaProdukClicked = onKelolaProdukClicked) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            HeaderSection()
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoCard(label = "Produk", value = state.listProduk.size.toString())
                InfoCard(label = "Transaksi", value = "89") // Data dummy
                InfoCard(label = "Pendapatan", value = "12.5M") // Data dummy
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("Quick Actions", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            // Card ini sudah benar, memanggil onKelolaProdukClicked
            KelolaProdukCard(onClick = onKelolaProdukClicked)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ActionCard(label = "Transaksi", imageResId = R.drawable.transaksi)
                ActionCard(label = "Laporan", imageResId = R.drawable.laporan)
                ActionCard(label = "Profile", imageResId = R.drawable.profile)
            }
        }
    }
}

// ... (HeaderSection, InfoCard, KelolaProdukCard, ActionCard tidak perlu diubah) ...
@Composable
fun HeaderSection() {
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
            Text("Admin Store", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
fun RowScope.ActionCard(label: String, imageResId: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = label,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}


// --- FUNGSI YANG DIPERBAIKI ---
@Composable
fun BottomNavigationBar(onKelolaProdukClicked: () -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Gray
    ) {
        // 1. Home (Selected)
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(28.dp)) },
            label = { Text("Home") },
            selected = true, // Ini adalah halaman Home, jadi 'selected' harus true
            onClick = {}, // Tidak perlu aksi karena sudah di halaman ini
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                indicatorColor = limeColor,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        // 2. Laporan
        NavigationBarItem(
            icon = { Image(painter = painterResource(id = R.drawable.laporan), contentDescription = "Laporan", modifier = Modifier.size(28.dp)) },
            label = { Text("Laporan") },
            selected = false,
            onClick = { /* TODO: Navigasi ke Halaman Laporan */ }
        )

        // 3. Kelola Produk (Ini yang benar)
        NavigationBarItem(
            icon = { Image(painter = painterResource(id = R.drawable.kelola), contentDescription = "Kelola Produk", modifier = Modifier.size(28.dp)) },
            label = { Text("Kelola") },
            selected = false,
            onClick = onKelolaProdukClicked // Gunakan callback di sini
        )

        // 4. Transaksi
        NavigationBarItem(
            icon = { Image(painter = painterResource(id = R.drawable.transaksi), contentDescription = "Transaksi", modifier = Modifier.size(28.dp)) },
            label = { Text("Transaksi") },
            selected = false,
            onClick = { /* TODO: Navigasi ke Halaman Transaksi */ }
        )

        // 5. Profile
        NavigationBarItem(
            icon = { Image(painter = painterResource(id = R.drawable.profile), contentDescription = "Profile", modifier = Modifier.size(28.dp)) },
            label = { Text("Profile") },
            selected = false,
            onClick = { /* TODO: Navigasi ke Halaman Profile */ }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHalamanHome() {
    ProjectAkhirTheme {
        // PERBAIKAN: Tambahkan lambda kosong agar Preview tidak error
        HalamanHome(onKelolaProdukClicked = {})
    }
}
