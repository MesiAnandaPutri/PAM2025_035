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

    // Menyimpan state dari UI (inputan pengguna)
    var uiStateProduk by mutableStateOf(UIStateProduk())
        private set

    // Fungsi untuk memperbarui state setiap kali ada inputan baru
    fun updateUIState(detailProduk: DetailProduk) {
        uiStateProduk = uiStateProduk.copy(
            detailProduk = detailProduk,
            isEntryValid = validasiInput(detailProduk)
        )
    }

    // Fungsi untuk menyimpan produk baru ke server
    suspend fun saveProduk() {
        if (validasiInput()) {
            try {
                repositoriDataProduk.postProduk(uiStateProduk.detailProduk.toDataProduk())
            } catch (e: IOException) {
                // Tangani error di sini, misalnya dengan menampilkan pesan
                e.printStackTrace()
            }
        }
    }

    // Fungsi untuk memvalidasi input
    private fun validasiInput(detailProduk: DetailProduk = uiStateProduk.detailProduk): Boolean {
        return with(detailProduk) {
            // Validasi sederhana: nama, kategori, dan satuan tidak boleh kosong.
            // Harga dan stok harus lebih dari 0.
            produk_name.isNotBlank() && kategori.isNotBlank() && unit.isNotBlank() && harga > 0 && stock_qty >= 0
        }
    }
}
