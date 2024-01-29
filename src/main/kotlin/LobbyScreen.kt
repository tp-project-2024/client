import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LobbyScreen(
    currentUserProfile: UserProfileDto,
    leaderboard: List<UserProfileDto>,
    onGameStart: () -> Unit,
    showError: (Throwable) -> Unit,
    token: String,
) {
    var showCurrentProfile by remember { mutableStateOf(false) }
    val showLeaderboardProfiles = mutableStateMapOf<UserProfileDto, Boolean>()
    leaderboard.forEach {
        showLeaderboardProfiles[it] = false
    }
    var friendInvites by remember {
        mutableStateOf<List<UserInviteDto>>(emptyList())
    }

    var gameInvites by remember {
        mutableStateOf<List<UserInviteDto>>(emptyList())
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            while(true) {
                println("launchedeffect")
                delay(1000)
                val friendInvitesResult = fetchInvites(
                    myId = currentUserProfile.userId,
                    token = token,
                    type = InviteType.FRIEND,
                )
                if (friendInvitesResult.isFailure) {
                    continue
                }
                friendInvites = friendInvitesResult.getOrDefault(emptyList())

                val gameInvitesResult = fetchInvites(
                    myId = currentUserProfile.userId,
                    token = token,
                    type = InviteType.GAME,
                )
                if (gameInvitesResult.isFailure) {
                    continue
                }
                gameInvites = friendInvitesResult.getOrDefault(emptyList())
            }
        }
    }

    Row {
        Column {
            Button(
                onClick = {
                    showCurrentProfile = true
                },
                content = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(25.dp),
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.background,
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(40.dp),
                elevation = ButtonDefaults.elevation(
                    0.dp,
                    0.dp,
                    0.dp,
                    0.dp,
                    0.dp,
                ),
                shape = CircleShape,
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
        )
        LazyColumn(
            horizontalAlignment = Alignment.End
        ) {
            items(leaderboard.size) { i ->
                UserProfileListable(
                    userProfile = leaderboard[i],
                    onClick = {
                        println("show $it")
                        showLeaderboardProfiles[it] = true
                    },
                )
            }
        }
    }

    if (showCurrentProfile) {
        UserProfilePopup(
            myId = currentUserProfile.userId,
            profile = currentUserProfile,
            isMe = true,
            onDismissRequest = { showCurrentProfile = false },
        )
    }

    showLeaderboardProfiles.forEach { (profile, show) ->
        if (show) {
            println("show $profile popup")
            UserProfilePopup(
                myId = currentUserProfile.userId,
                profile = profile,
                onDismissRequest = { showLeaderboardProfiles[profile] = false },
                onFriendInvite = { accept ->
                    if (!accept) {
                        val result = sendInvite(
                            myId = currentUserProfile.userId,
                            userId = profile.userId,
                            token = token,
                            type = InviteType.FRIEND,
                        )
                        if (result.isFailure) {
                            showError(result.exceptionOrNull()!!)
                        }
                    } else {
                        val result = acceptInvite(
                            invite = UserInviteDto(
                                userSenderId = profile.userId,
                                userReceiverId = currentUserProfile.userId
                            ),
                            token = token,
                            type = InviteType.FRIEND,
                        )
                        if (result.isFailure) {
                            showError(result.exceptionOrNull()!!)
                        }
                    }
                },
                onGameInvite = { accept ->
                    if (!accept) {
                        val result = sendInvite(
                            myId = currentUserProfile.userId,
                            userId = profile.userId,
                            token = token,
                            type = InviteType.GAME,
                        )
                        if (result.isFailure) {
                            showError(result.exceptionOrNull()!!)
                        }
                    } else {
                        val result = acceptInvite(
                            invite = UserInviteDto(
                                userSenderId = profile.userId,
                                userReceiverId = currentUserProfile.userId
                            ),
                            token = token,
                            type = InviteType.GAME,
                        )
                        if (result.isFailure) {
                            showError(result.exceptionOrNull()!!)
                        }

                        onGameStart()
                    }
                },
            )
        }
    }
}