package com.example.projectakhir.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectakhir.R
import com.example.projectakhir.modeldata.DetailProduk
import com.example.projectakhir.ui.theme.ProjectAkhirTheme
import com.example.projectakhir.viewmodel.EntryViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@Composable
fun HalamanEntry(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    entryViewModel: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState = entryViewModel.uiStateProduk

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tambah Produk Baru", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = limeColor, thickness = 2.dp, modifier = Modifier.width(50.dp))
            Spacer(modifier = Modifier.height(24.dp))

            // Form
            FormInputProduk(
                detailProduk = uiState.detailProduk,
                onValueChange = { entryViewModel.updateUIState(it) }
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateUp,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Batal")
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            entryViewModel.saveProduk()
                            onNavigateUp() // Kembali setelah menyimpan
                        }
                    },
                    enabled = uiState.isEntryValid, // Tombol aktif jika input valid
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = limeColor)
                ) {
                    Text("Tambah Produk", color = Color.Black)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputProduk(
    detailProduk: DetailProduk,
    onValueChange: (DetailProduk) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upload Gambar
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(limeColor.copy(alpha = 0.2f))
                    .border(1.dp, limeColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(id = R.drawable.kelola), contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-110).dp, y = 10.dp)
                    .clip(CircleShape)
                    .background(limeColor)
                    .clickable { /* TODO: Fungsi upload gambar */ }
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Gambar", tint = Color.Black)
            }
        }

        // Nama Produk
        OutlinedTextField(
            value = detailProduk.produk_name,
            onValueChange = { onValueChange(detailProduk.copy(produk_name = it)) },
            label = { Text("Nama Produk") },
            modifier = Modifier.fillMaxWidth()
        )

        // Kategori & Satuan
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DropdownMenuField(
                label = "Kategori",
                options = listOf("Makanan", "Minuman", "Snack", "Lainnya"),
                selectedValue = detailProduk.kategori,
                onValueChange = { onValueChange(detailProduk.copy(kategori = it)) },
                modifier = Modifier.weight(1f)
            )
            DropdownMenuField(
                label = "Satuan",
                options = listOf("Pcs", "Box", "Lusin", "Kg"),
                selectedValue = detailProduk.unit,
                onValueChange = { onValueChange(detailProduk.copy(unit = it)) },
                modifier = Modifier.weight(1f)
            )
        }

        // Stok & Harga
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = detailProduk.stock_qty.toString(),
                onValueChange = { onValueChange(detailProduk.copy(stock_qty = it.toIntOrNull() ?: 0)) },
                label = { Text("Stok") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = detailProduk.harga.toString(),
                onValueChange = { onValueChange(detailProduk.copy(harga = it.toIntOrNull() ?: 0)) },
                label = { Text("Harga (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHalamanEntry() {
    ProjectAkhirTheme {
        HalamanEntry(onNavigateUp = {})
    }
}
