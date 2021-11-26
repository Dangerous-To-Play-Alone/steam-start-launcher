package api

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import org.jetbrains.skija.impl.Log

class GameService() {
    private val client
        get() = HttpClient(Java) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }

    fun getGames(): Flow<GamesResponse> {
            return flow<GamesResponse> {
                this.emit(
                    client.get<GamesResponse>("http://api.steampowered.com/ISteamApps/GetAppList/v0002/")
                )
            }
                .onEach { Log.debug(it.toString()) }
                .catch { e -> Log.error(e.message) }
    }
}

@Serializable
data class GamesResponse(
    val applist: AppList
)

@Serializable
data class AppList(
    val apps: List<Application>
)

@Serializable
data class Application(
    val appid: Int,
    val name: String
)

