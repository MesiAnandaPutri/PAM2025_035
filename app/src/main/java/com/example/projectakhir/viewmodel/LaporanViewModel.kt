package com.example.projectakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectakhir.modeldata.DataProduk
import com.example.projectakhir.modeldata.HistoryLog
import com.example.projectakhir.repositori.RepositoriDataProduk
import kotlinx.coroutines.launch

data class LaporanUIState(
    val riwayatAktivitas: List<HistoryLog> = listOf(),
    val listProduk: List<DataProduk> = listOf(), // Ditambahkan agar tidak error
    val totalProduk: Int = 0,
    val stokMinimCount: Int = 0,
    val totalRestock: Int = 0,
    val isLoading: Boolean = false
)

class LaporanViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {
    var uiState by mutableStateOf(LaporanUIState())
        private set

    init {
        muatDataLaporan()
    }

    fun muatDataLaporan() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val produk = repositoriDataProduk.getProduk()
                val riwayat = repositoriDataProduk.getHistory()

                uiState = uiState.copy(
                    riwayatAktivitas = riwayat,
                    listProduk = produk,
                    totalProduk = produk.size,
                    stokMinimCount = produk.count { it.stock_qty < 5 },
                    totalRestock = riwayat.filter { it.tipe == "masuk" }.sumOf { it.jumlah },
                    isLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}