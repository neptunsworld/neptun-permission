package world.neptuns.permission.api.player

import kotlinx.coroutines.Deferred
import world.neptuns.streamline.api.utils.Updatable
import java.util.*

interface PermissionPlayerService : Updatable<UUID, PermissionPlayer.Update, Any> {

    suspend fun getPermissionPlayers(): Deferred<List<PermissionPlayer>>
    suspend fun getPermissionPlayer(uuid: UUID): PermissionPlayer?
    suspend fun getPermissionPlayerAsync(uuid: UUID): Deferred<PermissionPlayer?>

    suspend fun createPermissionPlayer(uuid: UUID)

    suspend fun deletePermissionPlayer(uuid: UUID)

    suspend fun loadPermissionPlayer(uuid: UUID): Deferred<PermissionPlayer?>

    suspend fun unloadPermissionPlayer(uuid: UUID)

    fun cachePermissionPlayer(uuid: UUID, permissionPlayer: PermissionPlayer)

    fun removeCachedPermissionPlayer(uuid: UUID)

}