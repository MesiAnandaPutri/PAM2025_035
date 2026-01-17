package com.example.projectakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectakhir.modeldata.RegisterRequest
import com.example.projectakhir.repositori.RepositoriDataProduk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class RegisterUIState(
    val username: String = "",
    val pass: String = "",
    val isLoading: Boolean = false
)

class RegisterViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {

    var registerUIState by mutableStateOf(RegisterUIState())
        private set

    fun updateUIState(newState: RegisterUIState) {
        registerUIState = newState
    }

    fun register(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (registerUIState.username.isBlank() || registerUIState.pass.isBlank()) {
            onError("Email dan Password tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            registerUIState = registerUIState.copy(isLoading = true)
            try {
                val request = RegisterRequest(
                    username = registerUIState.username,
                    password = registerUIState.pass,
                    user_role = "Staff" // Sudah sesuai ENUM('Admin', 'Staff')
                )

                val response = repositoriDataProduk.register(request)

                if (response.success) {
                    onSuccess()
                } else {
                    onError(response.message)
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                onError("Gagal mendaftar: Username mungkin sudah digunakan atau server bermasalah.")
            } catch (e: IOException) {
                onError("Masalah jaringan. Pastikan server aktif.")
            } catch (e: Exception) {
                onError("Terjadi kesalahan: ${e.message}")
            } finally {
                registerUIState = registerUIState.copy(isLoading = false)
            }
        }
    }
}