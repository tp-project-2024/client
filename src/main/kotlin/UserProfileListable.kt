import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserProfileListable(
    userProfileDto: UserProfileDto,
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
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