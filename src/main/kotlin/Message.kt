import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun Message(
    author: String,
    content: String,
    timestamp: String,
) {
    Row {
        Text(
            text = timestamp,
            fontSize = 12.sp,
            maxLines = 1,
        )
        Text(
            text = "$author: $content",
            fontSize = 14.sp,
            maxLines = 1,
        )
    }
}