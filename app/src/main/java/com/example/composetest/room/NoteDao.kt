package com.example.composetest.room

import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    suspend fun getAll(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrUpdate(note: Note)

    @Delete
    suspend fun delete(note: Note)
}