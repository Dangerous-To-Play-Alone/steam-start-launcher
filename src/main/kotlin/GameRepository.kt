import VDF.VDF
import api.Application
import api.GameService
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(
    private val gameService: GameService = GameService()
) {

    fun fetchOwnedGames(): Flow<List<Application>> = gameService.getGames()
        .map { games ->
            val vdf = VDF(File("C:\\Program Files (x86)\\Steam\\steamapps\\libraryfolders.vdf"))
            val gameIds = vdf.getParent("libraryfolders")?.parents?.map {
                it?.getParent("apps")?.keys
            }?.fold(emptyList<String?>()) { list, items -> list + items!! }

            games.applist.apps.filter { gameIds?.contains(it.appid.toString()) ?: false }
        }
}