package world.neptuns.permission.common.api.group

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.ResultRow
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupController
import world.neptuns.permission.common.repository.PermissionGroupCache
import world.neptuns.permission.common.repository.PermissionGroupRepository
import world.neptuns.streamline.api.NeptunStreamlineProvider
import java.util.*

@Suppress("OPT_IN_USAGE")
class PermissionGroupControllerImpl(override val updateChannel: String) : PermissionGroupController {

    private val permissionGroupRepository = NeptunStreamlineProvider.api.repositoryLoader.get(PermissionGroupRepository::class.java)!!
    private val permissionGroupCache = NeptunStreamlineProvider.api.cacheLoader.get(PermissionGroupCache::class.java)!!

    init {
        GlobalScope.launch(Dispatchers.IO) {
            permissionGroupRepository.onUpdate { name, permissionGroup ->
                if (!permissionGroupCache.contains(name)) return@onUpdate
                permissionGroupCache.update(name, permissionGroup)
            }
        }
    }

    override suspend fun getPermissionGroup(name: String): PermissionGroup? {
        TODO("Not yet implemented")
    }

    override suspend fun getPermissionGroups(): Set<PermissionGroup> {
        TODO("Not yet implemented")
    }

    override suspend fun getDefaultGroup(): PermissionGroup? {
        TODO("Not yet implemented")
    }

    override suspend fun deletePermissionGroup(name: String) {
        TODO("Not yet implemented")
    }

    override suspend fun createOrLoadEntry(key: UUID, defaultValue: PermissionGroup?, vararg data: Any) {
        TODO("Not yet implemented")
    }

    override suspend fun unloadEntry(key: UUID) {
        TODO("Not yet implemented")
    }

    override fun constructEntry(resultRow: ResultRow): PermissionGroup {
        TODO("Not yet implemented")
    }

    override fun cacheEntry(key: UUID, value: PermissionGroup) {
        TODO("Not yet implemented")
    }

    override fun removeEntryFromCache(key: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun bulkUpdateEntry(updateType: PermissionGroup.Update, key: UUID, newValue: Any, updateCache: Boolean, result: (Unit) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCachedEntry(updateType: PermissionGroup.Update, key: UUID, newValue: Any, result: (Unit) -> Unit) {
        TODO("Not yet implemented")
    }

}