import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
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

        LazyHorizontalGrid(
            rows = GridCells.Fixed(19),
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    boardSize = it
                },
            contentPadding = PaddingValues(25.dp),
        ) {
            val cellAmount = 19
            val cellSizeFraction = (cellAmount.toFloat() / boardSize.height)
                .coerceAtMost(cellAmount.toFloat() / boardSize.width)

            items(19 * 19) { x ->
                Image(
                    painter = painterResource("cell.svg"),
                    contentDescription = "cell",
                    modifier = Modifier
                        .fillMaxSize(cellSizeFraction)
                )
            }
        }
    }
}