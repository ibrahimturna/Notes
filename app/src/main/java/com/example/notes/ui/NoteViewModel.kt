package com.example.notes.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.feature_note.domain.model.Note
import com.example.notes.feature_note.data.data_source.NoteDao
import com.example.notes.feature_note.data.repository.NoteEvent
import com.example.notes.feature_note.data.repository.NoteState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NoteViewModel(private val dao: NoteDao) : ViewModel() {

    private val _notes =
        dao.getNotes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(NoteState())
    val state = combine(_state, _notes) { state, notes ->
        state.copy(
            notes = notes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteState())

    fun onEvent(event: NoteEvent) {
        when (event) {
            is NoteEvent.DeleteSingleNote -> {
                viewModelScope.launch {
                    dao.deleteSingleNote(event.noteId)
                }
                onEvent(NoteEvent.ResetNote)
            }

            NoteEvent.SaveNote -> {
                val title = state.value.title
                val description = state.value.description

                val note = Note(
                    title = title,
                    description = description,
                )

                if (state.value.id != 0) {
                    note.id = state.value.id
                }

                viewModelScope.launch {
                    val id = dao.upsertNote(note).toInt()
                    if (id != -1) {
                        onEvent(NoteEvent.SetId(id))
                    }
                }
            }

            is NoteEvent.SetTitle -> {
                _state.update {
                    it.copy(
                        title = event.title
                    )
                }
            }

            is NoteEvent.SetDescription -> {
                _state.update {
                    it.copy(
                        description = event.description
                    )
                }
            }

            is NoteEvent.SetId -> {
                _state.update {
                    it.copy(
                        id = event.id
                    )
                }
            }

            is NoteEvent.GetNote -> {
                val noteFlow = dao.getNote(event.noteId)
                runBlocking {
                    val note = noteFlow.first()
                    onEvent(NoteEvent.SetTitle(note.title))
                    onEvent(NoteEvent.SetDescription(note.description))
                }
            }

            is NoteEvent.SetSearch -> {
                _state.update {
                    it.copy(
                        search = mutableStateOf(TextFieldValue(event.search))
                    )
                }
            }

            is NoteEvent.ResetNote -> {
                _state.update {
                    it.copy(
                        title = "",
                        description = "",
                        id = 0
                    )
                }
            }

            is NoteEvent.ShowListView -> {
                _state.update {
                    it.copy(
                        isListView = true
                    )
                }
            }

            is NoteEvent.ShowStaggeredGridView -> {
                _state.update {
                    it.copy(
                        isListView = false
                    )
                }
            }

            is NoteEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        dialog = true
                    )
                }
            }

            is NoteEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        dialog = false
                    )
                }
            }

            is NoteEvent.NoteSelected -> {
                if (!state.value.notesToDelete.contains(event.id)) {
                    state.value.notesToDelete.add(event.id)
                    onEvent(NoteEvent.EndSelectionMode)
                    onEvent(NoteEvent.StartSelectionMode)
                }
            }

            is NoteEvent.ClearNotesDeletionList -> {
                state.value.notesToDelete.clear()
            }

            is NoteEvent.DeleteNotes -> {
                viewModelScope.launch {
                    dao.deleteNotes(event.idList)
                }
                onEvent(NoteEvent.ResetNote)
            }

            is NoteEvent.StartSelectionMode -> {
                _state.update {
                    it.copy(
                        selectionMode = true
                    )
                }
            }

            is NoteEvent.EndSelectionMode -> {
                _state.update {
                    it.copy(
                        selectionMode = false
                    )
                }
            }
        }
    }
}