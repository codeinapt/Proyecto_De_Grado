package com.example.modulos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val packetId: String,
    val senderId: String,
    val receiverId: String,
    val encryptedContent: String,
    val status: String, // PENDING, IN_MESH, SYNCED
    val timestamp: Long
)
