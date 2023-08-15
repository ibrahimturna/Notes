package com.example.notes.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.notes.feature_note.data.repository.NoteState
import com.example.todo.R

@Composable
fun AlertDialog(
    state: NoteState,
    onDismiss: () -> Unit,
    onConfirmClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            if (state.notesToDelete.size > 1) {
                Text(text = stringResource(R.string.alert_multiple_note_deletion))
            } else {
                Text(text = stringResource(R.string.alert_single_note_deletion))
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmClicked
            ) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.no))
            }
        }
    )
}