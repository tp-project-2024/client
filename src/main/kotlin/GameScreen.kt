import Globals.BASE_URL
import Globals.HTTP
import Globals.MOSHI
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Request

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameScreen(
    myId: Long,
    game: GameDto,
    //messages: List<GameMessageDto>,
    token: String,
    onMove: (GameJournalDto, StoneType) -> StoneType,
    //onMessage: (String) -> Unit,
    onGameEnd: (Long) -> Unit,
) {
    //var message by rememberSaveable(stateSaver = TextFieldValue.Saver) {
    //    mutableStateOf(TextFieldValue("", TextRange(0, 0)))
    //}

    //val messageAuthors = remember {
    //    mutableStateMapOf<Long, UserProfileDto>()
    //}

    var myTurn by remember { mutableStateOf(game.userWhiteId == myId) }

    val cellAmount = 19

    val board = remember {
        mutableStateListOf(
            mutableStateListOf<StoneType>()
        )
    }

    // seriously? manual list initialization in 2024?
    for (i in 0 until 19) {
        board.add(mutableStateListOf())
        for (ii in 0 until 19) {
            board[i].add(StoneType.EMPTY)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            while(true) {
                delay(5000)
                if (myTurn) {
                    continue
                }

                val request = Request.Builder()
                    .get()
                    .url("$BASE_URL/game/move/fetch")
                    .header("Authorization", "Bearer $token")
                    .build()

                val result = HTTP.newCall(request).execute().use request@{ response ->
                    if (!response.isSuccessful) {
                        return@request null
                    }

                    return@request MOSHI.adapter(GameJournalDto::class.java)
                        .fromJson(response.body!!.source())
                } ?: continue

                when (result.action) {
                    GameAction.MOVE -> {
                        board[result.turnY][result.turnX] =
                            if (result.authorId == game.userWhiteId) StoneType.WHITE
                                else StoneType.BLACK

                        myTurn = true
                    }
                    GameAction.WIN_REQ -> {
                        onGameEnd(if (myId == game.userWhiteId) game.userWhiteId else game.userBlackId)
                    }
                    GameAction.LOSE_REQ -> {
                        onGameEnd(if (myId == game.userWhiteId) game.userBlackId else game.userWhiteId)
                    }
                    GameAction.LEAVE -> {
                        onGameEnd(-1L)
                    }
                }
            }
        }
    }

    val colorToImagePath: (StoneType) -> String = { when (it) {
        StoneType.BLACK -> "black.svg"
        StoneType.WHITE -> "white.svg"
        StoneType.EMPTY -> "null.svg"
    }}

    val myStoneColor: StoneType = when (myId) {
        game.userBlackId -> StoneType.BLACK
        game.userWhiteId -> StoneType.WHITE
        else -> StoneType.EMPTY
    }

    Row(
        modifier = Modifier.fillMaxHeight()
    ) {
        //LazyColumn(
        //    modifier = Modifier
        //        .horizontalScroll(rememberScrollState())
        //) {
        //    items(messages.size) { i ->
        //        val currentMessage = messages[i]
        //        Message(
        //            author = messageAuthors.getOrPut(currentMessage.authorId) {
        //                getUserProfile(myId, currentMessage.authorId, token)
        //                    .getOrElse {
        //                        UserProfileDto.INVALID
        //                    }
        //            }.nickname,
        //            content = currentMessage.content,
        //            timestamp = currentMessage.timestamp,
        //        )
        //    }
        //    item {
        //        TextField(
        //            placeholder = { Text("Send message...") },
        //            value = message,
        //            onValueChange = {
        //                if (it.text.length <= 128)
        //                    message = it
        //            },
        //            singleLine = true,
        //            modifier = Modifier.onKeyEvent {
        //                if (it.key == Key.Enter) {
        //                    onMessage(message.text)
        //                    return@onKeyEvent true
        //                }
        //                return@onKeyEvent false
        //            },
        //        )
        //    }
        //}

        Column {
            val turn = if (myTurn) myStoneColor
                else if (myStoneColor == StoneType.WHITE) StoneType.BLACK
                    else StoneType.WHITE
            Text("Turn: $turn")
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
                    painter = if (board[y][x] == StoneType.EMPTY)
                        painterResource("cell.svg")
                    else painterResource(colorToImagePath(board[y][x])),
                    contentDescription = if (board[y][x] == StoneType.EMPTY)
                        "cell"
                    else "stone",
                    modifier = Modifier
                        .fillMaxSize(cellSizeFraction)
                        .onClick {
                            val resultColor = onMove(
                                GameJournalDto(
                                    gameId = game.gameId,
                                    authorId = myId,
                                    turnX = x,
                                    turnY = y,
                                    action = GameAction.MOVE,
                                ),
                                myStoneColor
                            )

                            if (resultColor == StoneType.EMPTY) {
                                return@onClick
                            }

                            board[y][x] = resultColor
                            myTurn = false
                        }
                )
            }
        }
    }
}