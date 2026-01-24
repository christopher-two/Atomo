package org.override.atomo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val socialLinks: String?, // JSON string
    val createdAt: Long,
    val updatedAt: Long
)
