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

@Composable
fun LobbyScreen(
    currentUserProfileDto: UserProfileDto,
    leaderboard: List<UserProfileDto>,
) {
    var showCurrentProfile by remember { mutableStateOf(false) }

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
                UserProfileListable(leaderboard[i])
            }
        }
    }

    if (showCurrentProfile) {
        UserProfilePopup(
            currentUserProfileDto
        ) {
            showCurrentProfile = false
        }
    }
}