package com.example.notes.feature_note.data.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import com.example.notes.feature_note.domain.model.Note

data class NoteState(
    val notes: List<Note> = emptyList(),
    val title: String = "",
    val description: String = "",
    val id: Int = 0,
    val search: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    val isListView: Boolean = true,
    val dialog: Boolean = false,
    val selectedId: Int = 0,
    val notesToDelete: MutableList<Int> = mutableListOf(),
    val selectionMode: Boolean = false
)