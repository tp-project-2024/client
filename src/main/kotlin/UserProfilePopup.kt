import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun UserProfilePopup(
    myId: Long,
    profile: UserProfileDto,
    isMe: Boolean = false,
    onDismissRequest: () -> Unit,
    onFriendInvite: (Boolean, UserInviteDto) -> Unit = { _, _ -> },
    onGameInvite: (Boolean, UserInviteDto) -> Unit = { _, _ -> },
    friendInvite: UserInviteDto? = null,
    gameInvite: UserInviteDto? = null,
) {
    val isInvitedToFriendsByMe = if (friendInvite == null) false
        else friendInvite.userSenderId == myId && friendInvite.userReceiverId == profile.userId
    val didInviteMeToFriends = if (friendInvite == null) false
        else friendInvite.userSenderId == profile.userId && friendInvite.userReceiverId == myId
    val isInvitedToGameByMe = if (gameInvite == null) false
        else gameInvite.userSenderId == myId && gameInvite.userReceiverId == profile.userId
    val didInviteMeToGame = if (gameInvite == null) false
        else gameInvite.userSenderId == profile.userId && gameInvite.userReceiverId == myId

    Popup(
        focusable = true,
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(
                        text = profile.nickname,
                        textAlign = TextAlign.Center,
                    )
                    if (!isMe) {
                        Divider()
                        Row {
                            Button(
                                onClick = {
                                    val inviteDto = UserInviteDto(
                                        userSenderId = if (didInviteMeToGame) profile.userId else myId,
                                        userReceiverId = if (didInviteMeToGame) myId else profile.userId,
                                    )
                                    onGameInvite(didInviteMeToGame, inviteDto)
                                },
                                enabled = !isInvitedToGameByMe,
                            ) {
                                Text(if (didInviteMeToGame) "Accept game invite" else "Invite to game")
                            }
                            Button(
                                onClick = {
                                    val inviteDto = UserInviteDto(
                                        userSenderId = if (didInviteMeToFriends) profile.userId else myId,
                                        userReceiverId = if (didInviteMeToFriends) myId else profile.userId,
                                    )
                                    onFriendInvite(didInviteMeToFriends, inviteDto)
                                },
                                enabled = !isInvitedToFriendsByMe,
                            ) {
                                Text(if (didInviteMeToFriends) "Accept friend invite" else "Invite friend")
                            }
                        }
                    }
                }
                Divider()
                Text(
                    text = profile.bio,
                )
            }
        }
    }
}