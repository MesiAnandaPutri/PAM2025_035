package com.example.projectakhir.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.projectakhir.R
import com.example.projectakhir.viewmodel.TransaksiViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTransaksi(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransaksiViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            // Panel Ringkasan Transaksi (Bagian Bawah)
            Surface(shadowElevation = 10.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Item", fontSize = 12.sp, color = Color.Gray)
                        Text("${viewModel.hitungTotalItem()} pcs", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Rp ${viewModel.hitungTotalHarga()}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            viewModel.simpanTransaksi(
                                onSuccess = { Toast.makeText(context, "Transaksi Berhasil!", Toast.LENGTH_SHORT).show() },
                                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
                            )
                        },
                        enabled = viewModel.hitungTotalItem() > 0 && !uiState.isLoading,
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth(0.7f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6F484)), // Warna kuning muda sesuai desain tombol
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                        } else {
                            Text("Simpan Transaksi", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD8FF00))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali ke Home",
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Input Transaksi", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.resetTransaksi() }) {
                        Text("Reset", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
                Text("Pilih produk untuk dijual", fontSize = 13.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.searchQuery, // Mengambil teks dari ViewModel
                    onValueChange = { viewModel.updateSearch(it) }, // Mengirim perubahan ke ViewModel
                    placeholder = { Text("Cari produk ") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        // Tambahkan tombol silang untuk menghapus teks pencarian jika tidak kosong
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearch("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Search")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp), // Beri sedikit padding agar tidak menempel ke tepi
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true // Pastikan hanya satu baris
                )
            }

            // List Produk
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.filteredProduk) { produk ->
                    val qtyInCart = viewModel.keranjang[produk.produk_id] ?: 0
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Produk
                            Surface(
                                modifier = Modifier.size(50.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF0F0F0)
                            ) {
                                val baseUrl = "http://10.0.2.2:3000/uploads/"
                                val fullUrl = "$baseUrl${produk.img_path}"
                                if (produk.img_path.isNotEmpty()) {
                                    AsyncImage(
                                        model = fullUrl,
                                        contentDescription = "Foto ${produk.produk_name}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        // Menampilkan icon kelola jika gambar sedang loading atau error
                                        placeholder = painterResource(id = R.drawable.kelola),
                                        error = painterResource(id = R.drawable.kelola)
                                    )
                                } else {
                                    // Jika path kosong di database
                                    Icon(
                                        painter = painterResource(id = R.drawable.kelola),
                                        contentDescription = null,
                                        modifier = Modifier.padding(10.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))

                            // Info Produk
                            Column(modifier = Modifier.weight(1f)) {
                                Text(produk.produk_name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Sisa Stok: ${produk.stock_qty}", color = Color(0xFF4CAF50), fontSize = 12.sp)
                                Text("Rp ${produk.harga}", color = Color(0xFF4CAF50), fontWeight = FontWeight.ExtraBold)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.kurangiDariKeranjang(produk.produk_id) }) {
                                    Image(
                                        painter = painterResource(id = R.drawable.remove),
                                        contentDescription = "Kurangi",
                                        modifier = Modifier.size(20.dp),
                                        alpha = if (qtyInCart > 0) 1f else 0.3f // Transparan jika 0
                                    )
                                }

                                Text(
                                    text = "$qtyInCart",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )

                                FilledIconButton(
                                    onClick = { viewModel.tambahKeKeranjang(produk.produk_id, produk.stock_qty) },
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFD8FF00))
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}