package com.example.composetest

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.composetest.room.Note
import com.example.composetest.room.NotesDatabase
import javax.inject.Inject

@WorkerThread
class NoteRepository @Inject constructor(
    private val notesDatabase: NotesDatabase
) {

    suspend fun getAll(): List<Note> {
        val notes = notesDatabase.noteDao().getAll()
        val sortedNotes = notes.sortedByDescending { it.id }
        for(note in sortedNotes) {
            Log.d("Notes", "Queried note $note")
        }
        return sortedNotes
    }

    suspend fun addNote(note: Note) {
        notesDatabase.noteDao().addOrUpdate(note)
    }

    suspend fun removeNote(note: Note) {
        notesDatabase.noteDao().delete(note)
    }

    suspend fun saveNotes(notes: List<Note>) {
        for(note in notes) {
            Log.d("Notes", "Saving note: $note")
            notesDatabase.noteDao().addOrUpdate(note)
        }
    }

}