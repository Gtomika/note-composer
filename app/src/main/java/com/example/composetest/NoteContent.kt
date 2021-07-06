package com.example.composetest

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.composetest.room.Note
import com.example.composetest.ui.theme.ComposeTestTheme
import kotlinx.coroutines.launch

@Composable
fun MainActivityContent(notesViewModel: NotesViewModel) {
    val scaffoldState = rememberScaffoldState()
    val noteAddedMessage = stringResource(id = R.string.note_added)
    Scaffold(
        topBar = { MainActivityTopBar() },
        floatingActionButton = {
            //pass the note added event to the floating action button
            NewNoteFloatingActionButton(
                onNoteAdded = { note ->
                    notesViewModel.onNoteAdded(note)
                    notesViewModel.viewModelScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(message = noteAddedMessage)
                    }
                },
                notesViewModel = notesViewModel
            ) },
        content = {
            ComposeTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    NotesContent(
                        notesViewModel = notesViewModel,
                        snackbarHostState = scaffoldState.snackbarHostState
                    )
                }
            }
        },
        scaffoldState = scaffoldState
    )
}

@Composable
fun MainActivityTopBar() {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) }
    )
}

@Composable
fun NewNoteFloatingActionButton(
    onNoteAdded: (Note) -> Unit,
    notesViewModel: NotesViewModel
) {
    if(notesViewModel.notesStatus() != NotesStatus.LOADING) {
        FloatingActionButton(
            onClick = { onNoteAdded(Note()) },
            content = {
                Icon(
                    painter = painterResource(android.R.drawable.ic_input_add),
                    contentDescription = stringResource(id = R.string.new_note)
                )
            }
        )
    }
}


@Composable
fun NotesContent( //State hoisting: make this composable stateless
    notesViewModel: NotesViewModel,
    snackbarHostState: SnackbarHostState
) {
    val notes = notesViewModel.notes.value
    when(notesViewModel.notesStatus()) {
        NotesStatus.NOT_EMPTY -> {
            //there are notes, make a scrollable column
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notes) { note ->
                    Log.d("Notes","Note added to lazy column: $note")
                    NoteContent(
                        note = note,
                        notesViewModel = notesViewModel,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
        NotesStatus.EMPTY -> {
            //no notes
            Text(
                text = stringResource(id = R.string.no_notes_added),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        NotesStatus.LOADING -> {
            //still loading
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun NoteContent(
    note: Note,
    notesViewModel: NotesViewModel,
    snackbarHostState: SnackbarHostState
) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 12.dp))
    ) {
        Column {
            val noteDeleteMessage = stringResource(id = R.string.note_deleted)
            val undo = stringResource(id = R.string.undo)
            NoteTitle(note = note)
            NoteText(note = note)
            NoteImportantCheckBox(note = note)
            NoteDeleteButton(
                note = note,
                onNoteRemoved = { note ->
                    notesViewModel.onNoteRemoved(note)
                    notesViewModel.viewModelScope.launch {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = noteDeleteMessage,
                            actionLabel = undo,
                            duration = SnackbarDuration.Short
                        )
                        when(snackbarResult) {
                            SnackbarResult.ActionPerformed -> notesViewModel.onNoteAdded(note)
                            SnackbarResult.Dismissed -> Log.d("Notes", "Delete snackbar dismissed.")
                        }
                    }
                },
            )
        }
    }
}

@Composable
fun NoteTitle(
    note: Note
) {
    val title = remember(note) { mutableStateOf(note.noteTitle) }
    OutlinedTextField (
        value = title.value,
        label = { Text(stringResource(id = R.string.title)) },
        onValueChange = {
            title.value = it
            note.noteTitle = it },
        textStyle = MaterialTheme.typography.h5,
        singleLine = true,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    )
}

@Composable
fun NoteText(
    note: Note
) {
    val text = remember(note) { mutableStateOf(note.noteText) }
    OutlinedTextField (
        value = text.value,
        label = { Text(stringResource(id = R.string.text)) },
        onValueChange = {
            text.value = it
            note.noteText = it },
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.body2
    )
}

@Composable
fun NoteImportantCheckBox(
    note: Note
) {
    val isImportant = remember(note) { mutableStateOf(note.isImportant) }
    Row {
        Checkbox(
            checked = isImportant.value,
            onCheckedChange = {
                isImportant.value = it
                note.isImportant = it },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Text(
            text = stringResource(id = R.string.important),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun NoteDeleteButton(
    note: Note,
    onNoteRemoved: (Note) -> Unit,
) {
    OutlinedButton(
        onClick = { onNoteRemoved(note) },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(stringResource(id = R.string.delete))
    }
}