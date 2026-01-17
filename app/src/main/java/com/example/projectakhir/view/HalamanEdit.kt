package com.example.projectakhir.view

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectakhir.ui.theme.ProjectAkhirTheme
import com.example.projectakhir.viewmodel.EditViewModel
import com.example.projectakhir.viewmodel.provider.PenyediaViewModel
import java.io.File

@Composable
fun HalamanEdit(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val uiState = viewModel.produkUiState

    // State lokal untuk menampung gambar baru yang dipilih dari galeri
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

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
            // --- Header ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Edit Produk", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = limeColor, thickness = 2.dp, modifier = Modifier.width(50.dp))
            Spacer(modifier = Modifier.height(24.dp))

            // --- Form Input ---
            // FormInputProduk akan otomatis menampilkan gambar lama dari server
            // atau gambar baru dari selectedImageUri
            FormInputProduk(
                detailProduk = uiState.detailProduk,
                onValueChange = { viewModel.updateUiState(uiState.copy(detailProduk = it)) },
                imageUri = selectedImageUri,
                onImageSelected = { selectedImageUri = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Action Buttons ---
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
                        // 1. Konversi URI ke File jika ada gambar baru yang dipilih
                        val imageFile = selectedImageUri?.let { uri ->
                            uriToFile(context, uri)
                        }

                        // 2. Panggil fungsi update khusus yang menangani upload gambar & data produk
                        viewModel.updateProdukWithImage(
                            file = imageFile,
                            onSuccess = {
                                Toast.makeText(context, "Perubahan berhasil disimpan", Toast.LENGTH_SHORT).show()
                                onNavigateUp()
                            },
                            onError = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    // Tombol aktif hanya jika validasi input terpenuhi
                    enabled = uiState.isEntryValid,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = limeColor)
                ) {
                    Text("Simpan Perubahan", color = Color.Black)
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewHalamanEdit() {
    ProjectAkhirTheme {
        HalamanEdit(onNavigateUp = {})
    }
}
