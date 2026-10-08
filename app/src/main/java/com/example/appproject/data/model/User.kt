package com.example.appproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @get:PropertyName("uid")
    val uid: String = "",

    @get:PropertyName("username")
    val username: String = "",

    @get:PropertyName("email")
    val email: String = "",

    @get:PropertyName("phone")
    val phone: String = "",

    @get:PropertyName("isBanned")
    val isBanned: Boolean = false,

    @get:PropertyName("isPhoneVerified")
    val isPhoneVerified: Boolean = false,

    @get:PropertyName("name")
    val name: String = ""
)
