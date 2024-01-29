import Globals.BASE_URL
import Globals.HTTP
import Globals.MOSHI
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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

enum class InviteType {
    FRIEND, GAME
}

fun sendInvite(
    invite: UserInviteDto,
    token: String,
    type: InviteType,
): Result<Unit> {
    val inviteDto = MOSHI.adapter(UserInviteDto::class.java)
        .toJson(invite)
    val pathBase = if (type == InviteType.FRIEND) "friend" else "game"
    val request = Request.Builder()
        .post(inviteDto.toRequestBody("application/json; charset=utf-8".toMediaType()))
        .url("$BASE_URL/$pathBase/invite/send")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use request@{ response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }
        return Result.success(Unit)
    }
}

fun acceptInvite(
    invite: UserInviteDto,
    token: String,
    type: InviteType,
): Result<UserInviteDto> {
    val inviteDto = MOSHI.adapter(UserInviteDto::class.java)
        .toJson(invite)
    val pathBase = if (type == InviteType.FRIEND) "friend" else "game"
    val request = Request.Builder()
        .post(inviteDto.toRequestBody("application/json; charset=utf-8".toMediaType()))
        .url("$BASE_URL/$pathBase/invite/accept")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use request@{ response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }

        return Result.success(
            MOSHI.adapter(UserInviteDto::class.java)
                .fromJson(response.body!!.source())
                ?: return Result.failure(ParseException("Failed to parse UserInviteDto", -1))
        )
    }
}

fun fetchInvites(
    myId: Long,
    token: String,
    type: InviteType,
): Result<List<UserInviteDto>> {
    val pathBase = if (type == InviteType.FRIEND) "friend" else "game"
    val request = Request.Builder()
        .get()
        .url("$BASE_URL/$pathBase/invite/fetch/$myId")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use request@{ response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }

        val inviteList = Types.newParameterizedType(List::class.java, UserInviteDto::class.java)

        val body = response.body!!.string()

        return Result.success(
            MOSHI.adapter<List<UserInviteDto>>(inviteList)
                .fromJson(body)
                ?: return Result.failure(ParseException("Failed to parse UserInviteDto", -1))
        )
    }
}

fun getCurrentGame(
    myId: Long,
    token: String,
): Result<GameDto> {
    val request = Request.Builder()
        .get()
        .url("$BASE_URL/game/invite/current/$myId")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use request@{ response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }

        return Result.success(
            MOSHI.adapter(GameDto::class.java)
                .fromJson(response.body!!.source())
                ?: return Result.failure(ParseException("Failed to parse UserInviteDto", -1))
        )
    }
}