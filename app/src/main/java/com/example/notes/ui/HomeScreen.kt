package com.example.notes.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notes.feature_note.presentation.util.Screen
import com.example.notes.feature_note.presentation.util.Util
import com.example.notes.feature_note.domain.model.Note
import com.example.notes.feature_note.data.repository.NoteEvent
import com.example.notes.feature_note.data.repository.NoteState
import com.example.todo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    state: NoteState,
    onEvent: (NoteEvent) -> Unit
) {
    val util = remember {
        Util(
            navController = navController,
            state = state,
            onEvent = onEvent
        )
    }
    Scaffold(
        topBar = {
            TopBar(
                modifier = modifier,
                state = state,
                onEvent = onEvent,
                onSelectionClosed = { util.closeSelection() },
                onShowDialog = { util.showDialog() }
            )
        },
        floatingActionButton = {
            FloatingActionButton {
                onEvent(NoteEvent.ResetNote)
                onEvent(NoteEvent.SaveNote)
                //navController.navigate(Screen.DetailScreen.route)
                onEvent(NoteEvent.EndSelectionMode)
                onEvent(NoteEvent.ClearNotesDeletionList)
                util.navigateTo(Screen.DetailScreen.route)
            }
        }
    ) { padding ->

        if (state.notes.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Center
            ) {
                Text(
                    text = stringResource(R.string.notes_empty)
                )
            }
        } else {
            if (state.isListView) {
                ListView(
                    modifier = modifier,
                    padding = padding,
                    navController = navController,
                    state = state,
                    onEvent = onEvent
                )
            } else {
                StaggeredGridView(
                    modifier = modifier,
                    padding = padding,
                    navController = navController,
                    state = state,
                    onEvent = onEvent
                )
            }
        }

        if (state.dialog) {
            AlertDialog(
                state = state,
                onDismiss = { onEvent(NoteEvent.HideDialog) }
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    onEvent(NoteEvent.HideDialog)
                    onEvent(NoteEvent.DeleteNotes(state.notesToDelete))
                    onEvent(NoteEvent.EndSelectionMode)
                    delay(1000)
                    onEvent(NoteEvent.ClearNotesDeletionList)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    state: NoteState,
    onEvent: (NoteEvent) -> Unit,
    onSelectionClosed: () -> Unit,
    onShowDialog: () -> Unit
) {
    TopAppBar(
        modifier = modifier.padding(vertical = 16.dp),
        title = {
            if (state.selectionMode) {
                IconButton(
                    onClick = onSelectionClosed
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            } else {
                SearchView(
                    modifier = modifier,
                    state = state.search
                )
            }
        },
        actions = {
            if (state.selectionMode) {
                IconButton(
                    onClick = onShowDialog
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            } else {
                IconButton(onClick = {
                    if (state.isListView) {
                        onEvent(NoteEvent.ShowStaggeredGridView)
                    } else {
                        onEvent(NoteEvent.ShowListView)
                    }
                }) {
                    Icon(
                        imageVector = if (state.isListView) {
                            Icons.Default.GridView
                        } else {
                            Icons.Outlined.ViewAgenda
                        },
                        contentDescription = stringResource(R.string.change_view)
                    )
                }
            }
        }
    )
}

@Composable
fun FloatingActionButton(onAddNewNote: () -> Unit) {
    FloatingActionButton(
        onClick = onAddNewNote,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_new_note)
        )
    }
}

@Composable
fun ListView(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    navController: NavController,
    state: NoteState,
    onEvent: (NoteEvent) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            top = padding.calculateTopPadding(),
            bottom = 8.dp,
            start = 8.dp,
            end = 8.dp
        )
    ) {

        items(items = state.notes.filter { note ->
            note.title.contains(state.search.value.text, ignoreCase = true) ||
                    note.description.contains(state.search.value.text, ignoreCase = true)
        }, key = { it.id }) { note ->

            Note(
                modifier = modifier,
                note = note,
                state = state,
                onNoteLongClicked = {
                    onEvent(NoteEvent.NoteSelected(note.id))
                },
                onNoteClicked = {
                    if (state.selectionMode) {
                        if (state.notesToDelete.contains(note.id)) {
                            state.notesToDelete.remove(note.id)
                            onEvent(NoteEvent.EndSelectionMode)
                            if (state.notesToDelete.size >= 1) {
                                onEvent(NoteEvent.StartSelectionMode)
                            }
                        } else {
                            onEvent(NoteEvent.EndSelectionMode)
                            onEvent(NoteEvent.NoteSelected(note.id))
                            onEvent(NoteEvent.StartSelectionMode)
                        }
                        println(state.notesToDelete)
                    } else {
                        onEvent(NoteEvent.GetNote(note.id))
                        navController.navigate(Screen.DetailScreen.withArgs(note.id))
                    }
                },
                noteId = note.id
            )
            Spacer(modifier = modifier.height(8.dp))
        }
    }
}

@Composable
fun StaggeredGridView(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    navController: NavController,
    state: NoteState,
    onEvent: (NoteEvent) -> Unit
) {
    LazyVerticalStaggeredGrid(
        contentPadding = PaddingValues(
            top = padding.calculateTopPadding(),
            bottom = 8.dp,
            start = 8.dp,
            end = 8.dp
        ),
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp
    ) {

        items(items = state.notes.filter { note ->
            note.title.contains(state.search.value.text, ignoreCase = true) ||
                    note.description.contains(state.search.value.text, ignoreCase = true)
        }, key = { it.id }) { note ->

            Note(
                modifier = modifier,
                note = note,
                state = state,
                onNoteLongClicked = {
                    onEvent(NoteEvent.NoteSelected(note.id))
                },
                onNoteClicked = {
                    if (state.selectionMode) {
                        if (state.notesToDelete.contains(note.id)) {
                            state.notesToDelete.remove(note.id)
                            onEvent(NoteEvent.EndSelectionMode)
                            if (state.notesToDelete.size >= 1) {
                                onEvent(NoteEvent.StartSelectionMode)
                            }
                        } else {
                            onEvent(NoteEvent.EndSelectionMode)
                            onEvent(NoteEvent.NoteSelected(note.id))
                            onEvent(NoteEvent.StartSelectionMode)
                        }
                        println(state.notesToDelete)
                    } else {
                        onEvent(NoteEvent.GetNote(note.id))
                        navController.navigate(Screen.DetailScreen.withArgs(note.id))
                    }
                },
                noteId = note.id
            )
            Spacer(modifier = modifier.height(8.dp))
        }
    }
}

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    state: MutableState<TextFieldValue>,
    placeHolder: String = stringResource(R.string.search)
) {
    Card(shape = RoundedCornerShape(40.dp)) {
        TextField(
            modifier = modifier.fillMaxWidth(),
            value = state.value,
            onValueChange = { value ->
                state.value = value
            },
            placeholder = { Text(text = placeHolder) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(15.dp)
                        .size(24.dp)
                )
            },
            trailingIcon = {
                if (state.value != TextFieldValue("")) {
                    IconButton(onClick = {
                        state.value = TextFieldValue("")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close, contentDescription = stringResource(
                                id = R.string.delete
                            )
                        )
                    }
                }
            },
            singleLine = true,
            textStyle = TextStyle(fontSize = 20.sp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Note(
    modifier: Modifier = Modifier,
    note: Note,
    state: NoteState,
    onNoteLongClicked: () -> Unit,
    onNoteClicked: () -> Unit,
    noteId: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            width = if (state.selectionMode && state.notesToDelete.contains(noteId)) {
                3.dp
            } else {
                1.dp
            },
            color = if (state.selectionMode && state.notesToDelete.contains(noteId)) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onNoteClicked,
                onLongClick = onNoteLongClicked
            )
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = modifier.fillMaxWidth(),
                text = note.description,
                maxLines = if (state.isListView) {
                    2
                } else {
                    5
                },
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}