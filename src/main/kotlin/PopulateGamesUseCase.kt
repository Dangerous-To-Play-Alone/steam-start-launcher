import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

class PopulateGamesUseCase(
    private val gameRepository: GameRepository = GameRepository()
) {

    operator fun invoke(scope: CoroutineScope) {
        gameRepository.fetchOwnedGames()
            .map {
                it.forEach { it.save(scope) }
            }
            .take(1)
            .launchIn(scope)
    }
}