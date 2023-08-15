package com.example.notes.feature_note.presentation.util

import androidx.navigation.NavController
import com.example.notes.feature_note.data.repository.NoteEvent
import com.example.notes.feature_note.data.repository.NoteState

class Util(
    private val navController: NavController,
    private val state: NoteState,
    private val onEvent: (NoteEvent) -> Unit
) {
    fun closeSelection() {
        onEvent(NoteEvent.EndSelectionMode)
        onEvent(NoteEvent.ClearNotesDeletionList)
    }

    fun showDialog() {
        onEvent(NoteEvent.ShowDialog)
    }

    fun navigateTo(route: String) {
        navController.navigate(route)
    }
}