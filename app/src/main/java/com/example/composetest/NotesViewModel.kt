package com.example.composetest

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetest.room.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository
): ViewModel() {

    var loading: Boolean = true

    //state: notes list
    val notes: MutableState<List<Note>> = mutableStateOf(listOf())

    init {
        viewModelScope.launch {
            notes.value = noteRepository.getAll()
            loading = false
        }
    }

    //event called by the UI: add new note
    fun onNoteAdded(note: Note) {
        Log.d("Notes","Before note added: ${notes.value}")
        notes.value = listOf(note).plus(notes.value)
        //save in database
        viewModelScope.launch {
            noteRepository.addNote(note = note)
        }
        Log.d("Notes","After note added: ${notes.value}")
    }

    //event called by the UI: remove existing note
    fun onNoteRemoved(note: Note) {
        Log.d("Notes","Before note removed: ${notes.value}")
        notes.value = notes.value.minus(note)
        viewModelScope.launch {
            noteRepository.removeNote(note = note)
        }
        Log.d("Notes","After note removed: ${notes.value}")
    }

    fun saveNotes() {
        viewModelScope.launch {
            noteRepository.saveNotes(notes.value)
        }
    }

    fun notesStatus() : NotesStatus {
        return when {
            loading -> NotesStatus.LOADING
            notes.value.isNotEmpty() -> NotesStatus.NOT_EMPTY
            else -> NotesStatus.EMPTY
        }
    }
}

enum class NotesStatus {
    NOT_EMPTY, EMPTY, LOADING
}