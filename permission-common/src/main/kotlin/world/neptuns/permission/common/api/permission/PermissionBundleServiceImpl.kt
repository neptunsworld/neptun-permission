package world.neptuns.permission.common.api.permission

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionBundleService
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.common.database.bundle.PermissionBundlePermissionInfoTable
import world.neptuns.permission.common.database.bundle.PermissionBundleTable
import world.neptuns.permission.common.database.bundle.dao.PermissionBundleEntity
import world.neptuns.permission.common.database.bundle.dao.PermissionBundlePermissionInfoEntity
import world.neptuns.permission.common.repository.PermissionBundleCache
import world.neptuns.permission.common.repository.PermissionBundleRepository
import world.neptuns.streamline.api.NeptunStreamlineProvider

@Suppress("OPT_IN_USAGE")
class PermissionBundleServiceImpl : PermissionBundleService {

    private val permissionBundleRepository = NeptunStreamlineProvider.api.repositoryLoader.get(PermissionBundleRepository::class.java)!!
    private val permissionBundleCache = NeptunStreamlineProvider.api.cacheLoader.get(PermissionBundleCache::class.java)!!

    init {
        GlobalScope.launch(Dispatchers.IO) {
            permissionBundleRepository.onUpdate { name, permissionBundle ->
                if (!permissionBundleCache.contains(name)) return@onUpdate
                permissionBundleCache.update(name, permissionBundle)
            }
        }
    }

    override suspend fun getPermissionBundle(name: String): PermissionBundle? {
        return this.permissionBundleCache.get(name) ?: this.permissionBundleRepository.get(name).await()
    }

    override suspend fun getPermissionBundles(): Set<PermissionBundle> {
        return this.permissionBundleCache.getAll().toSet()
    }

    override suspend fun createPermissionBundle(name: String, isAuthRequired: Boolean, timeoutTimestamp: Long) {
        newSuspendedTransaction {
            PermissionBundleEntity.new {
                this.name = name
                this.isAuthRequired = isAuthRequired
            }
        }

        val permissionBundle = PermissionBundleImpl(
            name,
            mutableSetOf(),
            isAuthRequired,
        )

        cachePermissionBundle(name, permissionBundle)
        this.permissionBundleRepository.insert(name, permissionBundle)
    }

    override suspend fun deletePermissionBundle(name: String) {
        newSuspendedTransaction {
            PermissionBundleEntity.find { PermissionBundleTable.name eq name }.singleOrNull()?.delete()
            unloadPermissionBundle(name)
        }
    }

    override suspend fun unloadPermissionBundle(name: String) {
        this.permissionBundleRepository.delete(name)
        this.permissionBundleCache.delete(name)
    }

    override fun cachePermissionBundle(name: String, permissionBundle: PermissionBundle) {
        this.permissionBundleCache.insert(name, permissionBundle)
    }

    override fun removeCachedPermissionBundle(name: String) {
        this.permissionBundleCache.delete(name)
    }

    override suspend fun bulkUpdateEntry(updateType: PermissionBundle.Update, key: String, newValue: Any, updateCache: Boolean, result: (Unit) -> Unit) {
        newSuspendedTransaction {
            when (updateType) {
                PermissionBundle.Update.PERMISSION_ADD -> {
                    PermissionBundlePermissionInfoEntity.new {
                        this.bundleName = key
                        this.permission = newValue as String
                    }
                }

                PermissionBundle.Update.PERMISSION_REMOVE -> {
                    PermissionBundlePermissionInfoEntity
                        .find { (PermissionBundlePermissionInfoTable.bundleName eq key) and (PermissionBundlePermissionInfoTable.permission eq newValue as String) }
                        .singleOrNull()
                        ?.delete()
                }

                PermissionBundle.Update.IS_AUTH_REQUIRED -> {
                    PermissionBundleEntity.find { PermissionBundleTable.name eq key }.singleOrNull()?.apply {
                        this.isAuthRequired = newValue as Boolean
                    }
                    commit()
                }
            }
        }

        if (updateCache) updateCachedEntry(updateType, key, newValue, result)
    }

    override suspend fun updateCachedEntry(updateType: PermissionBundle.Update, key: String, newValue: Any, result: (Unit) -> Unit) {
        val permissionBundle = getPermissionBundle(key) ?: return

        when (updateType) {
            PermissionBundle.Update.PERMISSION_ADD -> permissionBundle.permissions.add(newValue as PermissionInfo)
            PermissionBundle.Update.PERMISSION_REMOVE -> permissionBundle.permissions.removeIf { it.name == newValue as String }
            PermissionBundle.Update.IS_AUTH_REQUIRED -> permissionBundle.isAuthRequired = newValue as Boolean
        }

        permissionBundleRepository.update(key, permissionBundle)
    }

}