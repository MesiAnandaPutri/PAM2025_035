package com.example.projectakhir.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.projectakhir.repositori.AplikasiManageProduk
import com.example.projectakhir.viewmodel.EditViewModel
import com.example.projectakhir.viewmodel.EntryViewModel
import com.example.projectakhir.viewmodel.HomeViewModel
import com.example.projectakhir.viewmodel.KelolaProdukViewModel
import com.example.projectakhir.viewmodel.LaporanViewModel
import com.example.projectakhir.viewmodel.LoginViewModel
import com.example.projectakhir.viewmodel.TransaksiViewModel

object PenyediaViewModel {
    val Factory = viewModelFactory {
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
        initializer {
            EditViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                repositoriDataProduk = aplikasiManageProduk().container.repositoriDataProduk
            )
        }
        // Tambahkan di dalam object PenyediaViewModel.Factory
        initializer {
            TransaksiViewModel(aplikasiManageProduk().container.repositoriDataProduk)
        }
        // Tambahkan di dalam object PenyediaViewModel.Factory
        initializer {
            LaporanViewModel(aplikasiManageProduk().container.repositoriDataProduk)
        }
    }
}

fun CreationExtras.aplikasiManageProduk(): AplikasiManageProduk =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiManageProduk)
