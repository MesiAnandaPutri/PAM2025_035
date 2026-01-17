package com.example.projectakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectakhir.modeldata.LoginRequest
import com.example.projectakhir.repositori.RepositoriDataProduk
import kotlinx.coroutines.launch
import java.io.IOException

data class LoginUIState(
    val username: String = "",
    val pass: String = ""
)

class LoginViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {

    var loginUIState by mutableStateOf(LoginUIState())
        private set

    fun updateLoginUIState(inputState: LoginUIState) {
        loginUIState = inputState
    }

    fun tryLogin(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validasiInput()) {
            onError("Username dan Password tidak boleh kosong.")
            return
        }

        viewModelScope.launch {
            try {
                // PERBAIKAN: Panggil API login melalui repositori
                val loginRequest = LoginRequest(username = loginUIState.username, password = loginUIState.pass)
                val response = repositoriDataProduk.login(loginRequest)

                if (response.success) {
                    onSuccess() // Panggil callback sukses jika API mengembalikan success = true
                } else {
                    onError(response.message) // Tampilkan pesan error dari API
                }
            } catch (e: IOException) {
                onError("Gagal terhubung ke server. Periksa koneksi internet Anda.")
            } catch (e: Exception) {
                onError("Login gagal: ${e.message}")
            }
        }
    }

    private fun validasiInput(): Boolean {
        return loginUIState.username.isNotBlank() && loginUIState.pass.isNotBlank()
    }
}
