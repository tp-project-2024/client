import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient

object Globals {
    val BASE_URL = "https://go.jacekpoz.pl/api/v1"
    val HTTP = OkHttpClient()
    val MOSHI = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
}