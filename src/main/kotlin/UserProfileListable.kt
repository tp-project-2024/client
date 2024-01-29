import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserProfileListable(
    myId: Long,
    profile: UserProfileDto,
    onFriendInvite: (Boolean) -> Unit = {},
    onGameInvite: (Boolean) -> Unit = {},
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

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = profile.nickname,
            fontWeight = FontWeight.Bold,
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        Text(
            text = profile.score.toString(),
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        Text(
            text = profile.winsPerLosses.toString(),
        )
        if (myId != profile.userId) {
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )
            Row {
                Button(
                    onClick = {
                        onGameInvite(didInviteMeToGame)
                    },
                    enabled = !isInvitedToGameByMe,
                ) {
                    Text(if (didInviteMeToGame) "Accept game invite" else "Invite to game")
                }
                Button(
                    onClick = {
                        onFriendInvite(didInviteMeToFriends)
                    },
                    enabled = !isInvitedToFriendsByMe,
                ) {
                    Text(if (didInviteMeToFriends) "Accept friend invite" else "Invite friend")
                }
            }
        }
    }
}