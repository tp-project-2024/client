import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(
    messages: List<GameMessageDto>,
    token: String,
    onMove: () -> Unit,
    onMessage: (String) -> Unit,
) {
    var message by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 0)))
    }

    Row(
        modifier = Modifier.fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {
            items(messages.size) { i ->
                val currentMessage = messages[i]
                Message(
                    author = getUserProfile(currentMessage.authorId, token)
                        .getOrElse {
                            UserProfileDto.INVALID
                        }
                        .nickname,
                    content = currentMessage.content,
                    timestamp = currentMessage.timestamp,
                )
            }
            item {
                TextField(
                    placeholder = { Text("Send message...") },
                    value = message,
                    onValueChange = {
                        if (it.text.length <= 128)
                            message = it
                    },
                    singleLine = true,
                    modifier = Modifier.onKeyEvent {
                        if (it.key == Key.Enter) {
                            onMessage(message.text)
                            return@onKeyEvent true
                        }
                        return@onKeyEvent false
                    }
                )
            }
        }

        var boardSize by remember { mutableStateOf(IntSize.Zero) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    boardSize = it
                }
        ) {
            val cellAmount = 19
            val cellSize = boardSize / cellAmount

            Canvas(
                modifier = Modifier
                    .border(width = 2.dp, color = Color.Red)
            ) {
                for (y in (0..cellAmount)) {
                    for (x in (0..cellAmount)) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(x * cellSize.width + cellSize.width / 2.0f, y * cellSize.height + cellSize.height / 2.0f),
                            size = Size(cellSize.width.toFloat(), 1.0f),
                        )
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(x * cellSize.width + cellSize.width / 2.0f, y * cellSize.height + cellSize.height / 2.0f),
                            size = Size(1.0f, cellSize.height.toFloat()),
                        )
                    }
                }
            }

        }
    }
}