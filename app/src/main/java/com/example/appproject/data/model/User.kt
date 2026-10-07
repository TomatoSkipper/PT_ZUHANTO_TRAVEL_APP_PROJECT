package com.example.appproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @get:PropertyName("id")
    val id: Int = 0,

    @get:PropertyName("username")
    val username: String = "",

    @get:PropertyName("email")
    val email: String = "",

    @get:PropertyName("phone")
    val phone: String = "",

    @get:PropertyName("password")
    val password: String = "",

    @get:PropertyName("isBanned") @set:PropertyName("isBanned") @get:JvmName("isBanned")
    var isBanned: Boolean = false,

    @get:PropertyName("isPhoneVerified") @set:PropertyName("isPhoneVerified") @get:JvmName("isPhoneVerified")
    var isPhoneVerified: Boolean = false,

    @get:PropertyName("name")
    val name: String = ""
)

const val ADMIN_USERNAME = "Admin123"
