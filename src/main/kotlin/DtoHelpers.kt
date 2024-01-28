import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.Result

fun <V, E : Throwable> Result<V, E>.toStdResult(): kotlin.Result<V> =
    when (this) {
        is Result.Success -> kotlin.Result.success(this.get())
        is Result.Failure -> kotlin.Result.failure(this.failure())
    }

fun getLeaderboard(
    userId: Long,
    token: String,
): List<UserProfileDto> =
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
    "/leaderboard/$userId"
        .httpGet()
        .header("Authorization", "Bearer $token")
        .responseObject<List<LeaderboardDto>>()
        .third
        .toStdResult()
        .getOrElse {
            return emptyList()
        }
        .map {
            getUserProfile(it.userId, token)
                .getOrElse {
                    UserProfileDto.INVALID
                }
        }

fun getUserProfile(
    userId: Long,
    token: String,
): kotlin.Result<UserProfileDto> = try {
    "user/profile/$userId"
        .httpGet()
        .header("Authorization", "Bearer $token")
        .responseObject<UserProfileDto>()
        .third
        .toStdResult()
} catch (e: NoSuchMethodError) {
    kotlin.Result.failure(e)
}