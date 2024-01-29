import Globals.BASE_URL
import Globals.HTTP
import Globals.MOSHI
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
@Preview
fun App() {
    var loggedIn by remember { mutableStateOf(false) }
    var inGame by remember { mutableStateOf(false) }
    var accessToken by remember { mutableStateOf("") }
    var userProfile: UserProfileDto? = null
    val leaderboard: MutableList<UserProfileDto> = mutableListOf()
    var showError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    val setError: (Throwable) -> Unit = {
        errorText = it.message ?: "Unknown error"
        showError = true
    }

    val afterLogin: (authDto: AuthResponseDto) -> Unit = afterLogin@{ authDto: AuthResponseDto ->
        accessToken = authDto.token

        val userProfileResult = getUserProfile(authDto.userId, authDto.userId, accessToken)

        if (userProfileResult.isFailure) {
            setError(userProfileResult.exceptionOrNull()!!)
            return@afterLogin
        }

        userProfile = userProfileResult.getOrNull()!!

        loggedIn = true

        val leaderboardDto = getLeaderboard(userProfile!!.userId, accessToken)

        if (leaderboardDto.isFailure) {
            setError(leaderboardDto.exceptionOrNull()!!)
            return@afterLogin
        }

        leaderboard.addAll(leaderboardDto.getOrNull()!!)
    }

    val onLogin: (LoginDto) -> Unit = onLogin@{ loginDto: LoginDto ->
        val body = MOSHI.adapter(LoginDto::class.java)
            .toJson(loginDto)

        val request = Request.Builder()
            .post(body.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .url("$BASE_URL/auth/authenticate")
            .header("Authorization", "Bearer $accessToken")
            .build()

        val result = HTTP.newCall(request).execute().use newCall@{ response ->
            if (!response.isSuccessful) {
                return@newCall Result.failure(Exception(response.toString()))
            }

            return@newCall Result.success(
                MOSHI.adapter(AuthResponseDto::class.java)
                    .fromJson(response.body!!.source())
            )
        }

        if (result.isFailure) {
            setError(result.exceptionOrNull()!!)
            return@onLogin
        }

        val authDto = result.getOrNull()!!

        afterLogin(authDto)
    }

    val onRegister: (RegisterDto) -> Unit = onRegister@{ registerDto: RegisterDto ->
        val body = MOSHI.adapter(RegisterDto::class.java)
            .toJson(registerDto)

        val request = Request.Builder()
            .post(body.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .url("$BASE_URL/auth/register")
            .header("Authorization", "Bearer $accessToken")
            .build()

        val result = HTTP.newCall(request).execute().use newCall@{ response ->
            if (!response.isSuccessful) {
                return@newCall Result.failure(Exception(response.toString()))
            }

            return@newCall Result.success(
                MOSHI.adapter(AuthResponseDto::class.java)
                    .fromJson(response.body!!.source())
            )
        }

        if (result.isFailure) {
            setError(result.exceptionOrNull()!!)
            return@onRegister
        }

        val authDto = result.getOrNull()!!

        afterLogin(authDto)
    }


    MaterialTheme {
        if (loggedIn) {
            if (inGame) {
                GameScreen(
                    myId = 1L,
                    game = GameDto(
                        1L,
                        1L,
                        2L,
                    ),
                    token = accessToken,
                    messages = listOf(
                        GameMessageDto(
                            gameId = 1L,
                            authorId = 1L,
                            content = "siema",
                            timestamp = "22:21:35",
                        ),
                        GameMessageDto(
                            gameId = 1L,
                            authorId = 2L,
                            content = "cześć",
                            timestamp = "22:21:50",
                        ),
                        GameMessageDto(
                            gameId = 1L,
                            authorId = 1L,
                            content = "dupa",
                            timestamp = "22:22:07",
                        ),
                        GameMessageDto(
                            gameId = 1L,
                            authorId = 2L,
                            content = "dupa :D",
                            timestamp = "22:22:22",
                        ),
                    ),
                    onMessage = {

                    },
                    onMove = onMove@{ gameJournalDto, stoneColor ->
                        val body = MOSHI.adapter(GameJournalDto::class.java)
                            .toJson(gameJournalDto)

                        val request = Request.Builder()
                            .post(body.toRequestBody("application/json; charset=utf-8".toMediaType()))
                            .url("$BASE_URL/game/move/send")
                            .header("Authorization", "Bearer $accessToken")
                            .build()

                        val result = HTTP.newCall(request).execute().use newCall@{ response ->
                            if (!response.isSuccessful) {
                                return@newCall StoneType.EMPTY
                            }

                            return@newCall stoneColor
                        }

                        return@onMove result
                    },
                )
            } else {
                LobbyScreen(
                    currentUserProfile = userProfile!!,
                    leaderboard = leaderboard,
                    showError = {
                        setError(it)
                    },
                    token = accessToken,
                    onGameStart = { inGame = true },
                )
            }
        } else {
            LoginScreen(onLogin, onRegister)
        }
    }

    ErrorDialog(
        error = errorText,
        show = showError,
        onDismiss = { showError = false },
        onConfirm = { showError = false },
    )
}

fun main() = singleWindowApplication(
    title = "Go",
    state = WindowState(size = DpSize(800.dp, 600.dp))
) {
    App()
}
