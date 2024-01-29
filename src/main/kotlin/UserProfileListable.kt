import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserProfileListable(
    userProfileDto: UserProfileDto,
    onClick: (UserProfileDto) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .onClick {
                onClick(userProfileDto)
            },
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = userProfileDto.nickname,
            fontWeight = FontWeight.Bold,
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        Text(
            text = userProfileDto.score.toString(),
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        Text(
            text = userProfileDto.winsPerLosses.toString(),
        )
    }
}