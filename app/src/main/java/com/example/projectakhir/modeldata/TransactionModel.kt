package com.example.projectakhir.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    val produk_id: Int,
    val user_id: Int,
    val qty_out: Int,
    val tanggal: String = ""
)

@Serializable
data class TransactionResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class HistoryLog(
    val id: Int,
    val produk_name: String,
    val tipe: String, // "masuk" untuk restock, "keluar" untuk transaksi
    val jumlah: Int,
    val tanggal: String
)

@Serializable
data class HistoryResponse(
    val success: Boolean,
    val data: List<HistoryLog>
)