package com.example.notes.feature_note.data.data_source

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.notes.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Upsert
    suspend fun upsertNote(note: Note): Long

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteSingleNote(id: Int)

    @Query("DELETE FROM note where id in (:idList)")
    suspend fun deleteNotes(idList: List<Int>)

    @Query("SELECT * FROM note ORDER BY timestamp DESC")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getNote(id: Int): Flow<Note>

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?
}