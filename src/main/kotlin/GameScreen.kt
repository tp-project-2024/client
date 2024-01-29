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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun GameScreen(
    myId: Long,
    game: GameDto,
    messages: List<GameMessageDto>,
    token: String,
    onMove: (GameJournalDto) -> Unit,
    onMessage: (String) -> Unit,
) {
    var message by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 0)))
    }

    val colorToImagePath: (UserColor?) -> String = { when (it) {
        UserColor.BLACK -> "black.svg"
        UserColor.WHITE -> "white.svg"
        null -> "null.svg"
    }}

    val myStoneColor: UserColor? = when (myId) {
        game.userBlackId -> UserColor.BLACK
        game.userWhiteId -> UserColor.WHITE
        else -> null
    }

    val cellAmount = 19

    val board = remember {
        mutableStateListOf(
            mutableStateListOf<UserColor?>()
        )
    }

    // seriously? manual list initialization in 2024?
    for (i in 0 until 19) {
        board.add(mutableStateListOf())
        for (ii in 0 until 19) {
            board[i].add(null)
        }
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
                    author = getUserProfile(myId, currentMessage.authorId, token)
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
                    },
                )
            }
        }

        var boardSize by remember { mutableStateOf(IntSize.Zero) }

        LazyHorizontalGrid(
            rows = GridCells.Fixed(cellAmount),
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    boardSize = it
                },
            contentPadding = PaddingValues(25.dp),
        ) {
            val cellSizeFraction = (cellAmount.toFloat() / boardSize.height)
                .coerceAtMost(cellAmount.toFloat() / boardSize.width)

            items(cellAmount * cellAmount) { i ->
                val x = i % cellAmount
                val y = i / cellAmount

                Image(
                    painter = if (board[y][x] == null)
                        painterResource("cell.svg")
                    else painterResource(colorToImagePath(board[y][x])),
                    contentDescription = if (board[y][x] == null) "cell" else "stone",
                    modifier = Modifier
                        .fillMaxSize(cellSizeFraction)
                        .onClick {
                            board[y][x] = myStoneColor
                            onMove(
                                GameJournalDto(
                                    gameId = game.gameId,
                                    authorId = myId,
                                    turnX = x,
                                    turnY = y,
                                    action = GameAction.MOVE,
                                )
                            )
                        }
                )
            }
        }
    }
}