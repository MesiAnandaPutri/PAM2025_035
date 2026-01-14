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
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false
)

class RegisterViewModel(private val repositoriDataProduk: RepositoriDataProduk) : ViewModel() {

    var registerUIState by mutableStateOf(RegisterUIState())
        private set

    fun updateUIState(newState: RegisterUIState) {
        registerUIState = newState
    }

    // Lokasi: com/example/projectakhir/viewmodel/RegisterViewModel.kt

    fun register(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (registerUIState.email.isBlank() || registerUIState.pass.isBlank()) {
            onError("Email dan Password tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            registerUIState = registerUIState.copy(isLoading = true)
            try {
                val request = RegisterRequest(
                    username = registerUIState.email,
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
                // PERBAIKAN: Menangkap error 500 agar tidak Fatal Exception
                val errorBody = e.response()?.errorBody()?.string()
                // Biasanya 500 di register disebabkan username sudah terpakai
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