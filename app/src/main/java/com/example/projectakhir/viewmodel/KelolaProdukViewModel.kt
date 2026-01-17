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

data class KelolaProdukUIState(
    val listProduk: List<DataProduk> = listOf(),
    val filteredProduk: List<DataProduk> = listOf(), // Daftar yang sudah difilter
    val searchQuery: String = "", // Teks pencarian
    val produkForDeletion: DataProduk? = null,
    val produkForRestock: DataProduk? = null,
    val restockAmount: String = ""
)

class KelolaProdukViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {

    var kelolaProdukUIState by mutableStateOf(KelolaProdukUIState())
        private set

    init {
        getProduk()
    }

    fun getProduk() {
        viewModelScope.launch {
            try  {
                val produk = repositoriDataProduk.getProduk()
                kelolaProdukUIState = kelolaProdukUIState.copy(
                    listProduk = produk,
                    // Saat pertama kali dimuat, filteredProduk sama dengan listProduk
                    filteredProduk = if (kelolaProdukUIState.searchQuery.isEmpty()) produk
                    else produk.filter { it.produk_name.contains(kelolaProdukUIState.searchQuery, ignoreCase = true) }
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun setProdukForDeletion(produk: DataProduk) {
        kelolaProdukUIState = kelolaProdukUIState.copy(produkForDeletion = produk)
    }
    fun setProdukForRestock(produk: DataProduk) {
        kelolaProdukUIState = kelolaProdukUIState.copy(
            produkForRestock = produk,
            restockAmount = ""
        )
    }

    fun updateRestockAmount(amount: String) {
        if (amount.all { it.isDigit() }) {
            kelolaProdukUIState = kelolaProdukUIState.copy(restockAmount = amount)
        }
    }

    fun dismissRestockDialog() {
        kelolaProdukUIState = kelolaProdukUIState.copy(produkForRestock = null)
    }

    // Lokasi: com/example/projectakhir/viewmodel/KelolaProdukViewModel.kt

    fun restockProduk() {
        // Ambil jumlah dari state dan ID produk yang sedang dipilih
        val qty = kelolaProdukUIState.restockAmount.toIntOrNull() ?: 0
        val produkId = kelolaProdukUIState.produkForRestock?.produk_id

        if (qty > 0 && produkId != null) {
            viewModelScope.launch {
                try {
                    val response = repositoriDataProduk.restockProduct(produkId, qty)

                    if (response.success) {
                        // Jika sukses, tutup dialog dan refresh data produk
                        dismissRestockDialog()
                        getProduk()
                    }
                } catch (e: retrofit2.HttpException) {
                    println("DEBUG_RESTOCK: Server Error (500) - ${e.message}")
                } catch (e: Exception) {
                    println("DEBUG_RESTOCK: Terjadi kesalahan - ${e.message}")
                    e.printStackTrace()
                } finally {
                }
            }
        }
    }

    // --- FUNGSI BARU: Untuk menyembunyikan dialog ---
    fun dismissDeleteDialog() {
        kelolaProdukUIState = kelolaProdukUIState.copy(produkForDeletion = null)
    }
    fun deleteProduk() {
        viewModelScope.launch {
            try {
                kelolaProdukUIState.produkForDeletion?.let { produk ->
                    repositoriDataProduk.hapusSatuProduk(produk.produk_id)
                }
            } catch (e: retrofit2.HttpException) {
                println("DEBUG_HAPUS: Server Error (500) - ${e.message}")
            } catch (e: Exception) {
                println("DEBUG_HAPUS: Terjadi kesalahan - ${e.message}")
                e.printStackTrace()
            } finally {
                dismissDeleteDialog()
                getProduk()
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        val filtered = kelolaProdukUIState.listProduk.filter {
            it.produk_name.contains(newQuery, ignoreCase = true) ||
                    it.kategori.contains(newQuery, ignoreCase = true)
        }
        kelolaProdukUIState = kelolaProdukUIState.copy(
            searchQuery = newQuery,
            filteredProduk = filtered
        )
    }
}