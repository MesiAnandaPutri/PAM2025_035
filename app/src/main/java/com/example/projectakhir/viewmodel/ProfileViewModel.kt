package com.example.projectakhir.viewmodel

import androidx.activity.result.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectakhir.modeldata.UserData
import com.example.projectakhir.repositori.RepositoriDataProduk
import kotlinx.coroutines.launch

data class ProfileUIState(
    val username: String = "",
    val role: String = "",
    val listUsers: List<UserData> = listOf(),
    val isLoading: Boolean = false,
    val showUserList: Boolean = false // Untuk toggle tampilan list user
)

class ProfileViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {
    var profileUIState by mutableStateOf(ProfileUIState())
        private set

    init { muatProfil() }

    fun muatProfil() {
        val user = repositoriDataProduk.currentUser
        profileUIState = profileUIState.copy(
            username = user?.username ?: "Guest",
            role = user?.role ?: "User"
        )
    }

    fun toggleUserList() {
        val newState = !profileUIState.showUserList
        profileUIState = profileUIState.copy(showUserList = newState)
        if (newState) {
            getUsers()
        }
    }

    fun getUsers() {
        viewModelScope.launch {
        profileUIState = profileUIState.copy(isLoading = true)
        try {
            val users = repositoriDataProduk.getAllUsers()
            println("DEBUG_USERS: Jumlah user yang didapat: ${users.size}") // Tambahkan ini
            profileUIState = profileUIState.copy(
                listUsers = users,
                isLoading = false
            )
        } catch (e: Exception) {
            println("DEBUG_USERS: Error terjadi: ${e.message}") // Tambahkan ini
            profileUIState = profileUIState.copy(isLoading = false)
        }
    }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            profileUIState = profileUIState.copy(isLoading = true) // Set loading saat menghapus
            try {
                val response = repositoriDataProduk.deleteUser(id)
                if (response.success) {
                    // Jika berhasil, panggil kembali getUsers() untuk menyegarkan list
                    getUsers()
                } else {
                    profileUIState = profileUIState.copy(isLoading = false)
                }
            } catch (e: Exception) {
                println("DEBUG_USERS: Gagal menghapus user: ${e.message}")
                profileUIState = profileUIState.copy(isLoading = false)
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        repositoriDataProduk.currentUser = null
        onSuccess()
    }
}