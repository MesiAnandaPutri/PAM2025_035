package com.example.projectakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectakhir.modeldata.DataProduk
import com.example.projectakhir.repositori.RepositoriDataProduk
import kotlinx.coroutines.launch
import java.io.IOException

data class HomeUIState(
    val username: String = "",
    val role: String = "",
    val listProduk: List<DataProduk> = listOf(),
    val jumlahTransaksi: Int = 0,
    val totalPendapatan: Int = 0,
    val isLoading: Boolean = false
)

class HomeViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {
    var homeUIState by mutableStateOf(HomeUIState())
        private set

    fun getProduk() {
        viewModelScope.launch {
            homeUIState = homeUIState.copy(isLoading = true)
            try {
                val produk = repositoriDataProduk.getProduk()
                val history = repositoriDataProduk.getHistory()
                val listTransaksi = history.filter { it.tipe == "keluar" }

                val pendapatan = listTransaksi.sumOf { log ->
                    val p = produk.find { it.produk_name == log.produk_name }
                    (p?.harga ?: 0) * log.jumlah
                }
                val user = repositoriDataProduk.currentUser

                homeUIState = homeUIState.copy(
                    username = user?.username ?: "Guest", // Update Nama di sini
                    role = user?.role ?: "",
                    listProduk = produk,
                    jumlahTransaksi = listTransaksi.size,
                    totalPendapatan = pendapatan,
                    isLoading = false
                )

            } catch (e: Exception) {
                homeUIState = homeUIState.copy(isLoading = false)
            }
        }
    }
}