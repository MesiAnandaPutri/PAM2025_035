package com.example.projectakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.projectakhir.repositori.RepositoriDataProduk

data class ProfileUIState(
    val username: String = "Admin Store",
    val email: String = "admin@store.com",
    val role: String = "Administrator"
)
// Lokasi: viewmodel/ProfileViewModel.kt
class ProfileViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {

    var profileUIState by mutableStateOf(ProfileUIState())
        private set

    init {
        val user = repositoriDataProduk.currentUser
        profileUIState = ProfileUIState(
            username = user?.username ?: "Guest",
            email = if (user != null) "${user.username.lowercase()}@stokku.app" else "-",
            role = user?.role ?: "User"
        )
    }

    fun logout(onSuccess: () -> Unit) {
        repositoriDataProduk.currentUser = null
        onSuccess()
    }
}