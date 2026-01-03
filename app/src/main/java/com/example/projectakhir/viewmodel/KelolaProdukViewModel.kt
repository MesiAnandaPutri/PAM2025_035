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
    val listProduk: List<DataProduk> = listOf()
)

class KelolaProdukViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {

    var kelolaProdukUIState by mutableStateOf(KelolaProdukUIState())
        private set

    init {
        getProduk()
    }

    fun getProduk() {
        viewModelScope.launch {
            try {
                kelolaProdukUIState = kelolaProdukUIState.copy(
                    listProduk = repositoriDataProduk.getProduk()
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
