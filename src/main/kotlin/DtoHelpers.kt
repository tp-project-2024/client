import Globals.BASE_URL
import Globals.HTTP
import Globals.MOSHI
import com.squareup.moshi.Types
import okhttp3.Request
import java.io.IOException
import java.text.ParseException

fun getLeaderboard(
    userId: Long,
    token: String,
): Result<List<UserProfileDto>> {
//listOf(
//    UserProfileDto(
//        userId = userId,
//        nickname = "poz",
//        score = 100L,
//        isFriend = false,
//        bio = "dupa",
//        winsPerLosses = 1.05f,
//    ),
//    UserProfileDto(
//        userId = userId,
//        nickname = "romka",
//        score = 102L,
//        isFriend = true,
//        bio = "dupa2",
//        winsPerLosses = 1.15f,
//    ),
//)
    val request = Request.Builder()
        .get()
        .url("$BASE_URL/leaderboard/$userId")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use { response ->
        if (!response.isSuccessful)
            return Result.failure(IOException(response.message))

        val profileList = Types.newParameterizedType(List::class.java, UserProfileDto::class.java)

        val body = response.body!!.string()

        return Result.success(
            MOSHI.adapter<List<UserProfileDto>>(profileList)
                .fromJson(body)
                ?: return Result.failure(ParseException("Failed to parse UserProfileDto", -1))
        )
    }
}

fun getUserProfile(
    myId: Long,
    userId: Long,
    token: String,
): Result<UserProfileDto> {
    val request = Request.Builder()
        .get()
        .url("$BASE_URL/user/profile?idAuthor=$myId&idAbout=$userId")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            return Result.failure(IOException(response.message))
        }

        return Result.success(
        MOSHI.adapter(UserProfileDto::class.java)
                .fromJson(response.body!!.source())
                ?: return Result.failure(ParseException("Failed to parse UserProfileDto", -1))
        )
    }
}