package com.example.projectakhir.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectakhir.R
import com.example.projectakhir.modeldata.DataProduk
import com.example.projectakhir.ui.theme.ProjectAkhirTheme
import com.example.projectakhir.viewmodel.KelolaProdukViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanKelolaProduk(
    onBackClicked: () -> Unit,
    onEditClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
    kelolaProdukViewModel: KelolaProdukViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = kelolaProdukViewModel.kelolaProdukUIState

    // 1. Dialog Konfirmasi Hapus
    uiState.produkForDeletion?.let { produk ->
        DeleteConfirmationDialog(
            produk = produk,
            onConfirm = { kelolaProdukViewModel.deleteProduk() },
            onDismiss = { kelolaProdukViewModel.dismissDeleteDialog() }
        )
    }

    // 2. Dialog Input Restock
    uiState.produkForRestock?.let { produk ->
        RestockDialog(
            produkName = produk.produk_name,
            amount = uiState.restockAmount,
            onAmountChange = { kelolaProdukViewModel.updateRestockAmount(it) },
            onConfirm = { kelolaProdukViewModel.restockProduk() },
            onDismiss = { kelolaProdukViewModel.dismissRestockDialog() }
        )
    }

    // 3. Lifecycle Observer (Sudah benar)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                kelolaProdukViewModel.getProduk()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // --- Header (Sudah benar) ---
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

            // Search Bar (Sudah benar)
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
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        // --- Daftar Produk (PERBAIKAN DI SINI) ---
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.listProduk) { produk ->
                ProdukItem(
                    produk = produk,
                    onEditClicked = { onEditClicked(produk.produk_id) },
                    onDeleteClicked = { kelolaProdukViewModel.setProdukForDeletion(produk) },
                    onRestockClicked = { kelolaProdukViewModel.setProdukForRestock(produk) } // Tambahkan ini
                )
            }
        }
    }
}

@Composable
fun RestockDialog(
    produkName: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restock: $produkName") },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Jumlah Stok Masuk") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = limeColor)) {
                Text("Simpan", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun ProdukItem(
    produk: DataProduk,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onRestockClicked: () -> Unit,
    modifier: Modifier = Modifier) {
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
                        .background(Color.LightGray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: Tampilkan gambar produk jika img_path tersedia
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(produk.produk_name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${produk.kategori} - ${produk.unit}", fontSize = 14.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val stockColor = if (produk.stock_qty <= 10) Color.Red.copy(alpha = 0.3f) else limeColor.copy(alpha = 0.3f)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(stockColor)
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
                IconButton(onClick = onEditClicked) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
                IconButton(onClick = onDeleteClicked) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray)
                }
                IconButton(onClick = onRestockClicked) {
                    Icon(painterResource(id = R.drawable.lanjut), "Restock", tint = Color.DarkGray) // Gunakan icon yg sesuai
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    produk: DataProduk,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.Delete, "Hapus Icon", tint = Color.Red)
            }
        },
        title = {
            Text("Hapus Produk?", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        },
        text = {
            Text(
                text = "Apakah Anda yakin ingin menghapus ${produk.produk_name}? Tindakan ini tidak dapat dibatalkan.",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Hapus")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Batal")
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHalamanKelolaProduk() {
    ProjectAkhirTheme {
        HalamanKelolaProduk(
            onBackClicked = {},
            onEditClicked = {}
        )
    }
}

@Preview
@Composable
fun PreviewDeleteDialog() {
    ProjectAkhirTheme {
        DeleteConfirmationDialog(
            produk = DataProduk(1, "Coca Cola 330ml", "Minuman", "Pcs", 10, 5000, null, ""),
            onConfirm = {},
            onDismiss = {}
        )
    }
}

