package com.example.projectakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectakhir.modeldata.DetailProduk
import com.example.projectakhir.modeldata.UIStateProduk
import com.example.projectakhir.modeldata.toDataProduk
import com.example.projectakhir.repositori.RepositoriDataProduk
import kotlinx.coroutines.launch
import java.io.IOException

class EntryViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {

    var uiStateProduk by mutableStateOf(UIStateProduk())
        private set

    fun isAdmin(): Boolean {
        return repositoriDataProduk.currentUser?.role == "Admin"
    }

    fun updateUIState(detailProduk: DetailProduk) {
        uiStateProduk = uiStateProduk.copy(
            detailProduk = detailProduk,
            isEntryValid = validasiInput(detailProduk)
        )
    }

    fun saveProduk(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!isAdmin()) {
            onError("Akses ditolak: Hanya Admin yang dapat menambah produk.")
            return
        }
        if (!validasiInput()) {
            onError("Input tidak valid. Harap periksa kembali semua data.")
            return
        }

        // Gunakan viewModelScope untuk menjalankan operasi jaringan di background thread.
        viewModelScope.launch {
            try {
                // Panggil API untuk menyimpan produk.
                val response = repositoriDataProduk.postProduk(uiStateProduk.detailProduk.toDataProduk())
                if (response.success) {
                    // Jika API mengembalikan sukses, panggil callback onSuccess.
                    onSuccess()
                } else {
                    // Jika API mengembalikan gagal, panggil callback onError dengan pesan dari server.
                    onError(response.message)
                }
            } catch (e: IOException) {
                // Jika terjadi error koneksi jaringan.
                onError("Gagal menyimpan data. Periksa koneksi internet Anda.")
            } catch (e: Exception) {
                // Untuk error lainnya.
                onError("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    private fun validasiInput(detailProduk: DetailProduk = uiStateProduk.detailProduk): Boolean {
        return with(detailProduk) {
            produk_name.isNotBlank() && kategori.isNotBlank() && unit.isNotBlank() && harga > 0 && stock_qty >= 0
        }
    }
}
