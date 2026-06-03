package com.example.modulos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE status = 'PENDING'")
    suspend fun getPendingMessages(): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE packetId = :packetId")
    suspend fun getMessageById(packetId: String): MessageEntity?

    @Query("UPDATE messages SET status = :status WHERE packetId = :packetId")
    suspend fun updateMessageStatus(packetId: String, status: String)
    
    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    suspend fun getAllMessages(): List<MessageEntity>
}
