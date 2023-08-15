package com.example.notes.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notes.feature_note.data.repository.NoteEvent
import com.example.notes.feature_note.data.repository.NoteState
import com.example.todo.R

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    state: NoteState,
    onEvent: (NoteEvent) -> Unit,
    noteId: Int?
) {

    if (noteId != 0 && noteId != null)
        onEvent(NoteEvent.SetId(noteId))

    val focusRequesterTitle = remember { FocusRequester() }
    val focusRequesterDescription = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit) {
        when {
            state.title.isEmpty() -> focusRequesterTitle.requestFocus()
            state.description.isEmpty() -> focusRequesterDescription.requestFocus()
        }
    }

    Surface {
        Column(
            modifier = modifier
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                // Title TextField

                TextField(
                    value = state.title,
                    onValueChange = {
                        onEvent(NoteEvent.SetTitle(it))
                        onEvent(NoteEvent.SaveNote)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    textStyle = TextStyle(
                        fontSize = 30.sp
                    ),
                    placeholder = { Text(text = stringResource(R.string.title), fontSize = 30.sp) },
                    singleLine = true,
                    maxLines = 1,
                    modifier = modifier
                        .weight(1f)
                        .focusRequester(focusRequesterTitle),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                // Delete Button
                IconButton(onClick = { onEvent(NoteEvent.ShowDialog) }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
            // Description TextField
            TextField(
                value = state.description,
                onValueChange = {
                    onEvent(NoteEvent.SetDescription(it))
                    onEvent(NoteEvent.SaveNote)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                placeholder = { Text(text = stringResource(R.string.note), fontSize = 20.sp) },
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .focusRequester(focusRequesterDescription)
            )
        }
    }
    if (state.dialog) {
        AlertDialog(
            state = state,
            onDismiss = { onEvent(NoteEvent.HideDialog) }
        ) {
            onEvent(NoteEvent.DeleteSingleNote(state.id))
            onEvent(NoteEvent.HideDialog)
            navController.navigateUp()
        }
    }
}
