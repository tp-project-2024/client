import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.jackson.objectBody
import com.github.kittinunf.fuel.jackson.responseObject

@Composable
@Preview
fun App() {
    var loggedIn by remember { mutableStateOf(false) }
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

        val userProfileResult = getUserProfile(authDto.userId, accessToken)

        if (userProfileResult.isFailure) {
            setError(userProfileResult.exceptionOrNull()!!)
            return@afterLogin
        }

        userProfile = userProfileResult.getOrNull()!!

        loggedIn = true

        leaderboard.addAll(getLeaderboard(userProfile!!.userId, accessToken))
    }

    val onLogin: (LoginDto) -> Unit = onLogin@{ loginDto: LoginDto ->
        val result = try {
                "/auth/authenticate"
                    .httpPost()
                    .objectBody(loginDto)
                    .responseObject<AuthResponseDto>()
                    .third
                    .toStdResult()
        } catch (e: NoSuchMethodError) {
            Result.failure(e)
        }

        if (result.isFailure) {
            setError(result.exceptionOrNull()!!)
            return@onLogin
        }

        val authDto = result.getOrNull()!!

        afterLogin(authDto)
    }

    val onRegister: (RegisterDto) -> Unit = onRegister@{ registerDto: RegisterDto ->
        val result = try {
            "/auth/register"
                .httpPost()
                .objectBody(registerDto)
                .responseObject<AuthResponseDto>()
                .third
                .toStdResult()
        } catch (e: NoSuchMethodError) {
            Result.failure(e)
        }

        if (result.isFailure) {
            setError(result.exceptionOrNull()!!)
            return@onRegister
        }

        val authDto = result.getOrNull()!!

        afterLogin(authDto)
    }


    //MaterialTheme {
    //    if (loggedIn) {
    //        LobbyScreen(userProfile!!, leaderboard)
    //    } else {
    //        LoginScreen(onLogin, onRegister)
    //    }
    //}
    GameScreen(
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
        token = accessToken,
        onMessage = {},
        onMove = {},
    )

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
    FuelManager.instance.basePath = "http://localhost:8080/api/v1"
    App()
}
