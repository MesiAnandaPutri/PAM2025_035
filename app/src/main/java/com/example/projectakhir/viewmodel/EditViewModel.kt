package com.example.projectakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectakhir.modeldata.UIStateProduk
import com.example.projectakhir.modeldata.toDataProduk
import com.example.projectakhir.modeldata.toUiStateProduk
import com.example.projectakhir.repositori.RepositoriDataProduk
import com.example.projectakhir.uicontroller.route.DestinasiEdit
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class EditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoriDataProduk: RepositoriDataProduk
) : ViewModel() {

    // Mengambil ID produk dari argumen navigasi
    private val produkId: Int = checkNotNull(savedStateHandle[DestinasiEdit.produkIdArg])

    var produkUiState by mutableStateOf(UIStateProduk())
        private set

    init {
        // Saat ViewModel dibuat, langsung ambil data produk dari repositori
        viewModelScope.launch {
            produkUiState = repositoriDataProduk.getSatuProduk(produkId)
                .toUiStateProduk(isEntryValid = true) // Set isEntryValid ke true karena data sudah ada
        }
    }

    // Fungsi untuk memperbarui UI state saat pengguna mengedit form
    fun updateUiState(uiStateProduk: UIStateProduk) {
        produkUiState = uiStateProduk.copy(isEntryValid = validasiInput(uiStateProduk))
    }

    // Fungsi untuk mengirim perubahan ke API
    fun updateProduk(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validasiInput(produkUiState)) {
            onError("Input tidak valid.")
            return
        }

        viewModelScope.launch {
            try {
                val response = repositoriDataProduk.editSatuProduk(
                    produkId,
                    produkUiState.detailProduk.toDataProduk()
                )
                if (response.success) {
                    onSuccess()
                } else {
                    onError(response.message)
                }
            } catch (e: IOException) {
                onError("Gagal terhubung ke server. Periksa koneksi Anda.")
            } catch (e: Exception) {
                onError("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    private fun validasiInput(uiState: UIStateProduk = produkUiState): Boolean {
        return with(uiState.detailProduk) {
            produk_name.isNotBlank() && kategori.isNotBlank() && unit.isNotBlank() && harga > 0 && stock_qty >= 0
        }
    }

    fun updateProdukWithImage(file: File?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                var finalPath = produkUiState.detailProduk.img_path

                // 1. Jika user memilih gambar baru, upload dulu
                if (file != null) {
                    val res = repositoriDataProduk.uploadImage(file)
                    if (res.success) {
                        finalPath = res.filename ?: finalPath
                    }
                }

                // 2. Kirim data ke API PUT
                // Pastikan produk_id yang dikirim sesuai dengan ID produk yang sedang diedit
                val produkData = produkUiState.detailProduk.copy(img_path = finalPath).toDataProduk()
                val response = repositoriDataProduk.editSatuProduk(produkId, produkData)

                if (response.success) {
                    onSuccess()
                } else {
                    // Tampilkan pesan error spesifik dari server
                    onError(response.message ?: "Gagal memperbarui produk")
                }
            } catch (e: Exception) {
                // Menangani crash jika server mati atau error 500
                e.printStackTrace()
                onError("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }
}
