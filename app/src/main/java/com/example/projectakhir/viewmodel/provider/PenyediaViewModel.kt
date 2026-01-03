package com.example.projectakhir.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.projectakhir.repositori.AplikasiManageProduk
import com.example.projectakhir.viewmodel.EntryViewModel
import com.example.projectakhir.viewmodel.HomeViewModel
import com.example.projectakhir.viewmodel.KelolaProdukViewModel
import com.example.projectakhir.viewmodel.LoginViewModel

object PenyediaViewModel {
    val Factory = viewModelFactory {

        // --- KESALAHAN #2: Initializer LoginViewModel salah ---
        // PERBAIKAN: Sediakan repositori saat membuat LoginViewModel
        initializer {
            LoginViewModel(aplikasiManageProduk().container.repositoriDataProduk)
        }

        initializer {
            HomeViewModel(aplikasiManageProduk().container.repositoriDataProduk)
        }
        initializer {
            KelolaProdukViewModel(aplikasiManageProduk().container.repositoriDataProduk)
        }
        initializer {
            EntryViewModel(aplikasiManageProduk().container.repositoriDataProduk)
        }
    }
}

fun CreationExtras.aplikasiManageProduk(): AplikasiManageProduk =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiManageProduk)
