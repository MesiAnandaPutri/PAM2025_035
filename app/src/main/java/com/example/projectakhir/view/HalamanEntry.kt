package com.example.projectakhir.view

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.projectakhir.R
import com.example.projectakhir.modeldata.DetailProduk
import com.example.projectakhir.ui.theme.ProjectAkhirTheme
import com.example.projectakhir.viewmodel.EntryViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel
import java.io.File

@Composable
fun HalamanEntry(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    entryViewModel: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // coroutineScope tidak lagi diperlukan di sini
    val uiState = entryViewModel.uiStateProduk
    val context = LocalContext.current // Diperlukan untuk menampilkan Toast
    val isAdmin = entryViewModel.isAdmin()

    val imageUri = entryViewModel.imageUri

    LaunchedEffect(isAdmin) {
        if (!isAdmin) {
            Toast.makeText(context, "Hanya Admin yang diizinkan mengakses halaman ini", Toast.LENGTH_SHORT).show()
            onNavigateUp()
        }
    }

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
                onValueChange = { entryViewModel.updateUIState(it) },
                imageUri = imageUri,
                onImageSelected = { entryViewModel.imageUri = it }
            )
            Spacer(modifier = Modifier.height(32.dp))
            // Tombol
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
                        val imageFile = entryViewModel.imageUri?.let { uri ->
                            uriToFile(context, uri)
                        }
                        // PERBAIKAN: Panggil fungsi saveProduk dari ViewModel dengan callbacks
                        entryViewModel.saveProdukWithImage(
                            file = imageFile,
                            onSuccess = {
                                Toast.makeText(context, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show()
                                onNavigateUp()
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = uiState.isEntryValid && isAdmin, // Tombol aktif jika input valid
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
// Sisa kode di HalamanEntry.kt (FormInputProduk, DropdownMenuField, Preview) tidak ada perubahan.
// ...


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputProduk(
    detailProduk: DetailProduk,
    onValueChange: (DetailProduk) -> Unit,
    imageUri: Uri?,
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val baseUrl = "http://10.0.2.2:3000/uploads/"
    val fullUrl = baseUrl + detailProduk.img_path

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }
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
                    .border(1.dp, limeColor, RoundedCornerShape(12.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ){
                if (imageUri != null) {
                    // Tampilkan gambar BARU yang baru dipilih dari galeri
                    AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize())
                } else if (detailProduk.img_path.isNotEmpty()) {
                    // Tampilkan gambar LAMA yang sudah ada di server
                    AsyncImage(model = fullUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
                } else {
                    // Tampilkan icon Tambah jika benar-benar kosong
                    Icon(Icons.Default.Add, contentDescription = null)
                }
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
fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
    tempFile.outputStream().use { output ->
        inputStream?.copyTo(output)
    }
    return tempFile
}


@Preview(showBackground = true)
@Composable
fun PreviewHalamanEntry() {
    ProjectAkhirTheme {
        HalamanEntry(onNavigateUp = {})
    }
}
