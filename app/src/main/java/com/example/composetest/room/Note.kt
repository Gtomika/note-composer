package com.example.composetest.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = false) val id: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "note_title") var noteTitle: String = "",
    @ColumnInfo(name = "note_text") var noteText: String = "",
    @ColumnInfo(name = "is_important") var isImportant: Boolean = false
)