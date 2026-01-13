package com.example.uas.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KasirDao {

    // Simpan data baru & update saat edit (REPLACE by PrimaryKey)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: KasirEntity)

    // Update data transaksi (opsional, tapi bagus secara akademik)
    @Update
    suspend fun update(data: KasirEntity)

    // Hapus satu transaksi
    @Delete
    suspend fun delete(data: KasirEntity)

    // Ambil semua riwayat transaksi (real-time)
    @Query("SELECT * FROM kasir ORDER BY timestamp DESC")
    fun getAll(): Flow<List<KasirEntity>>
}
