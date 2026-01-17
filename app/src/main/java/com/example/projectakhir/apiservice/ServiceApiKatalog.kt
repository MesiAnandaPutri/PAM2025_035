package com.example.projectakhir.apiservice

import com.example.projectakhir.modeldata.AddProductResponse
import com.example.projectakhir.modeldata.BaseResponse
import com.example.projectakhir.modeldata.DataProduk
import com.example.projectakhir.modeldata.HistoryResponse
import com.example.projectakhir.modeldata.LoginRequest
import com.example.projectakhir.modeldata.LoginResponse
import com.example.projectakhir.modeldata.ProductResponse
import com.example.projectakhir.modeldata.RegisterRequest
import com.example.projectakhir.modeldata.RestockRequest
import com.example.projectakhir.modeldata.SingleProductResponse
import com.example.projectakhir.modeldata.TransactionRequest
import com.example.projectakhir.modeldata.TransactionResponse
import com.example.projectakhir.modeldata.UserData
import com.example.projectakhir.modeldata.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ServiceApiKatalog {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): BaseResponse
    @GET("api/products")
    suspend fun getProduk(): ProductResponse
    @POST("api/products")
    suspend fun postProduk(@Body dataProduk: DataProduk): AddProductResponse
    @GET("api/products/{id}")
    suspend fun getSatuProduk(@Path("id") id: Int): SingleProductResponse
    @PUT("api/products/{id}")
    suspend fun editSatuProduk(@Path("id") id: Int, @Body dataProduk: DataProduk): BaseResponse
    @DELETE("api/products/{id}")
    suspend fun hapusSatuProduk(@Path("id") id: Int): BaseResponse
    @POST("api/products/restock/{id}")
    suspend fun restockProduct(@Path("id") id: Int, @Body request: RestockRequest): BaseResponse
    @POST("api/transactions")
    suspend fun createTransaction(@Body request: TransactionRequest): TransactionResponse
    @GET("api/history")
    suspend fun getHistory(): HistoryResponse
    @GET("api/users")
    suspend fun getAllUsers(): UserResponse
    // Tambahkan di interface ServiceApiKatalog
    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): BaseResponse
    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): AddProductResponse
}
