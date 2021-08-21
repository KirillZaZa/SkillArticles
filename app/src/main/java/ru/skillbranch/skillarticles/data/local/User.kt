package ru.skillbranch.skillarticles.data.local

import java.io.Serializable


data class User(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val rating: Int = 0,
    val respect: Int = 0,
    val about: String? = null
): Serializable {
}