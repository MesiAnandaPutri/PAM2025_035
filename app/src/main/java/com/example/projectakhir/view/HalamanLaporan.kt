package com.example.projectakhir.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectakhir.R
import com.example.projectakhir.viewmodel.LaporanViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanLaporan(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LaporanViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.muatDataLaporan()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(modifier = modifier
        .fillMaxSize()
        .background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(limeColor)
                .padding(top = 40.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.Black
                    )
                }
            }
            Text("Laporan Stok", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("Riwayat aktivitas stok produk", fontSize = 14.sp, color = Color.DarkGray)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                label = "Total Produk",
                value = uiState.totalProduk.toString(),
                imageRes = R.drawable.kelola, // MENGGUNAKAN kelola.png
                color = Color(0xFFD8FF00),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Stok Minim",
                value = uiState.stokMinimCount.toString(),
                icon = Icons.Default.Warning, // Tetap gunakan Icon untuk stok minim
                color = Color(0xFFFFEBEE),
                contentColor = Color.Red,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Restock",
                value = uiState.totalRestock.toString(),
                imageRes = R.drawable.trendingup, // MENGGUNAKAN trendingup.png
                color = Color(0xFFE8F5E9),
                contentColor = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        // --- SECTION TITLE ---
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Aktivitas Terbaru", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("10 transaksi tercatat", fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- PERIOD SELECTOR ---
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Periode Laporan", fontSize = 10.sp, color = Color.Gray)
                    Text("Hari Ini", fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- ACTIVITY LIST ---
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.riwayatAktivitas) { log ->
                val isMasuk = log.tipe == "masuk"
                ActivityItem(
                    name = log.produk_name,
                    date = log.tanggal, // Contoh: "13 Jan, 14:00"
                    amount = if (isMasuk) "+${log.jumlah}" else "-${log.jumlah}",
                    isPositive = isMasuk
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    label: String,
    value: String,
    imageRes: Int? = null, // Tambahan parameter untuk Resource Gambar
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    color: Color,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.Black
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = color) {
                Box(contentAlignment = Alignment.Center) {
                    if (imageRes != null) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (icon != null) {
                        Icon(
                            icon,
                            null,
                            modifier = Modifier.padding(8.dp),
                            tint = if(contentColor == Color.Black) Color.DarkGray else contentColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}

@Composable
fun ActivityItem(name: String, date: String, amount: String, isPositive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(20.dp),
                color = if(isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(
                            // isPositive (Masuk/Restock) = Panah Hijau Naik (trendingup)
                            // !isPositive (Keluar/Transaksi) = Panah Merah Turun (stokkeluar)
                            id = if (isPositive) R.drawable.trendingup else R.drawable.stokkeluar
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(date, fontSize = 11.sp, color = Color.Gray)
            }
            Text(
                amount,
                fontWeight = FontWeight.Bold,
                // Warna teks angka sesuai tipe
                color = if(isPositive) Color(0xFF4CAF50) else Color.Red
            )
        }
    }
}