package com.example.projectakhir.repositori

import com.example.projectakhir.apiservice.ServiceApiKatalog
import com.example.projectakhir.modeldata.*

interface RepositoriDataProduk {
    // Auth
    suspend fun login(loginRequest: LoginRequest): LoginResponse
    suspend fun register(registerRequest: RegisterRequest): BaseResponse

    // Produk
    // PERBAIKAN: Kembalikan List<DataProduk> langsung agar ViewModel tidak perlu tahu tentang ProductResponse
    suspend fun getProduk(): List<DataProduk>
    suspend fun getSatuProduk(id: Int): DataProduk // Kembalikan DataProduk langsung
    suspend fun postProduk(dataProduk: DataProduk): AddProductResponse
    suspend fun editSatuProduk(id: Int, dataProduk: DataProduk): BaseResponse
    suspend fun hapusSatuProduk(id: Int): BaseResponse

    // Transaksi & Restock
    suspend fun restockProduct(id: Int, qtyIn: Int): BaseResponse
    suspend fun createTransaction(produkId: Int, userId: Int, qtyOut: Int): TransactionResponse
    suspend fun getHistory(): List<HistoryLog>

    var currentUser: UserData?
}


class NetworkRepositoriDataProduk(
    private val serviceApiKatalog: ServiceApiKatalog
) : RepositoriDataProduk {


    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return try {
            val response = serviceApiKatalog.login(loginRequest)
            if (response.success) {
                currentUser = response.data
            }
            response
        } catch (e: retrofit2.HttpException) {
            LoginResponse(success = false, message = "Email atau Password salah (500)")
        } catch (e: Exception) {
            LoginResponse(success = false, message = "Gagal login: ${e.message}")
        }
    }
    override suspend fun register(registerRequest: RegisterRequest): BaseResponse {
        return try {
            serviceApiKatalog.register(registerRequest)
        } catch (e: retrofit2.HttpException) {
            // Jika server 500, kirim pesan gagal yang rapi
            BaseResponse(success = false, message = "Username sudah terdaftar atau server error (500)")
        } catch (e: Exception) {
            BaseResponse(success = false, message = "Kesalahan: ${e.message}")
        }
    }
    // PERBAIKAN: Ambil respons dari API, lalu ekstrak dan kembalikan list .data
    override suspend fun getProduk(): List<DataProduk> {
        return serviceApiKatalog.getProduk().data
    }

    // PERBAIKAN: Ambil respons dari API, lalu ekstrak dan kembalikan objek .data
    override suspend fun getSatuProduk(id: Int): DataProduk {
        return serviceApiKatalog.getSatuProduk(id).data
    }

    override suspend fun postProduk(dataProduk: DataProduk) = serviceApiKatalog.postProduk(dataProduk)
    override suspend fun editSatuProduk(id: Int, dataProduk: DataProduk) = serviceApiKatalog.editSatuProduk(id, dataProduk)
    override suspend fun hapusSatuProduk(id: Int) = serviceApiKatalog.hapusSatuProduk(id)

    override suspend fun restockProduct(id: Int, qtyIn: Int): BaseResponse {
        return serviceApiKatalog.restockProduct(id, RestockRequest(qtyIn))
    }

    override suspend fun createTransaction(produkId: Int, userId: Int, qtyOut: Int): TransactionResponse {
        val request = TransactionRequest(produk_id = produkId, user_id = userId, qty_out = qtyOut)
        return serviceApiKatalog.createTransaction(request)
    }
    override suspend fun getHistory(): List<HistoryLog> {
        return serviceApiKatalog.getHistory().data
    }
    override var currentUser: UserData? = null


}
