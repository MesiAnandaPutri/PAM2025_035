package com.example.projectakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectakhir.modeldata.DataProduk
import com.example.projectakhir.modeldata.TransactionRequest
import com.example.projectakhir.repositori.RepositoriDataProduk
import kotlinx.coroutines.launch
import java.io.IOException

data class TransaksiUIState(
    val listProduk: List<DataProduk> = listOf(),
    val filteredProduk: List<DataProduk> = listOf(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TransaksiViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {

    var uiState by mutableStateOf(TransaksiUIState())
        private set

    // Keranjang menggunakan Map: Key = ID Produk, Value = Jumlah Beli
    var keranjang = mutableStateMapOf<Int, Int>()
        private set

    init {
        muatProduk()
    }

    fun muatProduk() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val produk = repositoriDataProduk.getProduk()
                uiState = uiState.copy(listProduk = produk,
                    filteredProduk = if (uiState.searchQuery.isEmpty()) produk
                    else produk.filter { it.produk_name.contains(uiState.searchQuery, ignoreCase = true) },
                            isLoading = false)
            } catch (e: IOException) {
                uiState = uiState.copy(errorMessage = "Gagal memuat data produk", isLoading = false)
            }
        }
    }

    fun updateSearch(query: String) {
        val filtered = if (query.isEmpty()) {
            uiState.listProduk
        } else {
            uiState.listProduk.filter {
                it.produk_name.contains(query, ignoreCase = true) ||
                        it.kategori.contains(query, ignoreCase = true)
            }
        }
        uiState = uiState.copy(searchQuery = query, filteredProduk = filtered)
    }

    fun tambahKeKeranjang(produkId: Int, stokTersedia: Int) {
        val jumlahSekarang = keranjang[produkId] ?: 0
        if (jumlahSekarang < stokTersedia) {
            keranjang[produkId] = jumlahSekarang + 1
        }
    }

    fun kurangiDariKeranjang(produkId: Int) {
        val jumlahSekarang = keranjang[produkId] ?: 0
        if (jumlahSekarang > 0) {
            keranjang[produkId] = jumlahSekarang - 1
            // Jika jumlah jadi 0, hapus dari map agar tidak menumpuk memori
            if (keranjang[produkId] == 0) keranjang.remove(produkId)
        }
    }

    fun hitungTotalItem(): Int = keranjang.values.sum()

    fun hitungTotalHarga(): Int {
        return keranjang.entries.sumOf { (id, qty) ->
            val produk = uiState.listProduk.find { it.produk_id == id }
            (produk?.harga ?: 0) * qty
        }
    }

    fun resetTransaksi() {
        keranjang.clear()
        uiState = uiState.copy(searchQuery = "", filteredProduk = uiState.listProduk)
    }

    // Di dalam simpanTransaksi
    fun simpanTransaksi(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val tanggalSekarang = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())

                keranjang.filter { it.value > 0 }.forEach { (id, qty) ->
                    val request = TransactionRequest(
                        produk_id = id,
                        user_id = 1, // Sesuaikan dengan ID user login
                        qty_out = qty,
                        tanggal = tanggalSekarang
                    )
                    // Pastikan repositori memproses objek request ini
                    repositoriDataProduk.createTransaction(request.produk_id, request.user_id, request.qty_out)
                }
                resetTransaksi()
                muatProduk()
                onSuccess()
            } catch (e: Exception) {
                onError("Gagal: ${e.message}")
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}