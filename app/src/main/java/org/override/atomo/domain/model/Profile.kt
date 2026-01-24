package org.override.atomo.domain.model

data class Profile(
    val id: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val socialLinks: Map<String, String>?,
    val createdAt: Long,
    val updatedAt: Long
)
