import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient

object Globals {
    val BASE_URL = "http://localhost:8080/api/v1"
    val HTTP = OkHttpClient()
    val MOSHI = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
}