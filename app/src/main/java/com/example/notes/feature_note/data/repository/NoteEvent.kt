package com.example.notes.feature_note.data.repository

sealed interface NoteEvent {
    object SaveNote : NoteEvent
    object ResetNote: NoteEvent
    object ShowListView: NoteEvent
    object ShowStaggeredGridView: NoteEvent
    object ShowDialog: NoteEvent
    object HideDialog: NoteEvent
    object StartSelectionMode: NoteEvent
    object EndSelectionMode: NoteEvent
    object ClearNotesDeletionList: NoteEvent
    data class SetTitle(val title: String) : NoteEvent
    data class SetDescription(val description: String) : NoteEvent
    data class SetId(val id: Int) : NoteEvent
    data class SetSearch(val search: String) : NoteEvent
    data class DeleteSingleNote(val noteId: Int) : NoteEvent
    data class DeleteNotes(val idList: List<Int>) : NoteEvent
    data class GetNote(val noteId: Int) : NoteEvent
    data class NoteSelected(val id: Int) : NoteEvent
}