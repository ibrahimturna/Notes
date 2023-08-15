package com.example.notes.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    var title: String,
    var description: String,
    var timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)