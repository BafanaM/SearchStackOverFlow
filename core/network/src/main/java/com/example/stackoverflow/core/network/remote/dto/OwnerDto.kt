package com.example.stackoverflow.core.network.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerDto(
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("reputation") val reputation: Long? = null,
    @SerialName("profile_image") val profileImage: String? = null,
    @SerialName("user_id") val userId: Long? = null,
)
