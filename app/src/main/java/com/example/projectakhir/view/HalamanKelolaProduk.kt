package com.example.projectakhir.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
// --- IMPORT BARU ---
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
// --------------------
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- IMPORT BARU ---
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
// --------------------
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectakhir.R
import com.example.projectakhir.modeldata.DataProduk
import com.example.projectakhir.ui.theme.ProjectAkhirTheme
import com.example.projectakhir.viewmodel.KelolaProdukViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanKelolaProduk(
    onBackClicked: () -> Unit,
    onAddClicked: () -> Unit,
    modifier: Modifier = Modifier,
    kelolaProdukViewModel: KelolaProdukViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = kelolaProdukViewModel.kelolaProdukUIState

    // --- PERBAIKAN: Tambahkan observer siklus hidup untuk memuat ulang data ---
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // Jika event adalah RESUMED (halaman kembali aktif setelah dari halaman lain)
            if (event == Lifecycle.Event.ON_RESUME) {
                // Panggil fungsi untuk memuat ulang data produk
                kelolaProdukViewModel.getProduk()
            }
        }
        // Tambahkan observer ke lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(observer)

        // Hapus observer saat Composable dihancurkan untuk menghindari memory leak
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    // -----------------------------------------------------------------------

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClicked,
                containerColor = limeColor,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
            }
        },
        bottomBar = {
            KelolaProdukBottomBar(
                onHomeClicked = { /* TODO: Navigasi ke Home */ },
                onLaporanClicked = { /* TODO: Navigasi ke Laporan */ },
                onKelolaClicked = { /* Sudah di sini */ },
                onTransaksiClicked = { /* TODO: Navigasi ke Transaksi */ },
                onProfileClicked = { /* TODO: Navigasi ke Profile */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(limeColor)
                    .padding(16.dp)
            ) {
                IconButton(onClick = onBackClicked) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Kelola Produk", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("${uiState.listProduk.size} produk tersedia", fontSize = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Cari nama barang...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                    trailingIcon = { Icon(painterResource(id = R.drawable.logo), contentDescription = "Filter") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            // Daftar Produk
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.listProduk) { produk ->
                    ProdukItem(produk = produk)
                }
            }
        }
    }
}

// ... Sisa kode (ProdukItem, KelolaProdukBottomBar, dll) tidak perlu diubah ...
// Pastikan tidak ada perubahan pada sisa file ini.

@Composable
fun ProdukItem(produk: DataProduk, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.5f))
                ) {
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(produk.produk_name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${produk.kategori} - ${produk.unit}", fontSize = 14.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val stockColor = if (produk.stock_qty <= 10) Color.Red.copy(alpha = 0.7f) else limeColor
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(stockColor.copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Stok: ${produk.stock_qty}", fontSize = 12.sp, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rp ${produk.harga}", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            Row {
                IconButton(onClick = { /* TODO: Navigasi ke Halaman Edit */ }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
                IconButton(onClick = { /* TODO: Tampilkan dialog hapus */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun KelolaProdukBottomBar(
    onHomeClicked: () -> Unit,
    onLaporanClicked: () -> Unit,
    onKelolaClicked: () -> Unit,
    onTransaksiClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomBottomNavItem(
            iconRes = R.drawable.laporan, // Sebaiknya diganti dengan ikon home
            label = "Home",
            isSelected = false,
            onClick = onHomeClicked
        )
        CustomBottomNavItem(
            iconRes = R.drawable.laporan,
            label = "Laporan",
            isSelected = false,
            onClick = onLaporanClicked
        )
        CustomBottomNavItem(
            iconRes = R.drawable.kelola,
            label = "Kelola",
            isSelected = true,
            onClick = onKelolaClicked
        )
        CustomBottomNavItem(
            iconRes = R.drawable.transaksi,
            label = "Transaksi",
            isSelected = false,
            onClick = onTransaksiClicked
        )
        CustomBottomNavItem(
            iconRes = R.drawable.profile,
            label = "Profile",
            isSelected = false,
            onClick = onProfileClicked
        )
    }
}

@Composable
fun RowScope.CustomBottomNavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) Color.Black else Color.Gray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) limeColor else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = contentColor, fontSize = 12.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHalamanKelolaProduk() {
    ProjectAkhirTheme {
        HalamanKelolaProduk(
            onBackClicked = {},
            onAddClicked = {}
        )
    }
}
