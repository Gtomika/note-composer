package com.example.composetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //pass in view model, so the composables can call the events exposed by it
            MainActivityContent(notesViewModel = notesViewModel)
        }
    }

    override fun onPause() {
        super.onPause()
        notesViewModel.saveNotes()
    }

}