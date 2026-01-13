package com.example.uas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kasir")
data class KasirEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namaPembeli: String,
    val jumlahAyam: Int,
    val hargaAyam: Int,
    val totalHarga: Int,
    val timestamp: Long = System.currentTimeMillis()
)
