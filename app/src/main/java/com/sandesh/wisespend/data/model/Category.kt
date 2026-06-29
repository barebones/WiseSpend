package com.sandesh.wisespend.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// For expense categories
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val name: String,
    val iconKey: String,
    val isDefault: Boolean = false
)