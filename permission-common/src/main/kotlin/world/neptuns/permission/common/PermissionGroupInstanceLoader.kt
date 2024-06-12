package world.neptuns.permission.common

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextColor
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.PermissionApi
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.common.api.bundle.PermissionBundleImpl
import world.neptuns.permission.common.api.group.PermissionGroupImpl
import world.neptuns.permission.common.api.permission.PermissionInfoImpl
import world.neptuns.permission.common.database.bundle.dao.PermissionBundleEntity
import world.neptuns.permission.common.database.group.dao.PermissionGroupEntity
import world.neptuns.permission.common.repository.PermissionBundleRepository
import world.neptuns.permission.common.repository.PermissionGroupRepository
import world.neptuns.streamline.api.NeptunStreamlineProvider

object PermissionGroupInstanceLoader {

    suspend fun load() = withContext(Dispatchers.IO) {
        val permissionBundles: Set<PermissionBundle> = loadPermissionBundles().await()
        //TODO: impl metadata
        val permissionGroups: Set<PermissionGroup> = loadPermissionGroups(permissionBundles).await()

        println("Loaded ${permissionGroups.size} permission groups...")
    }

    private suspend fun loadPermissionBundles(): Deferred<Set<PermissionBundle>> = withContext(Dispatchers.IO) {
        suspendedTransactionAsync {
            val permissionBundles = mutableSetOf<PermissionBundleImpl>()

            for (permissionBundleEntity in PermissionBundleEntity.all()) {
                val permissionBundle = PermissionBundleImpl(
                    permissionBundleEntity.name,
                    permissionBundleEntity.permissions.map { PermissionInfoImpl(it.permission, -1L) }.toMutableSet(),
                    permissionBundleEntity.isAuthRequired,
                    permissionBundleEntity.timeoutTimestamp
                )

                NeptunStreamlineProvider.api.repositoryLoader.get(PermissionBundleRepository::class.java)?.insert(permissionBundle.name, permissionBundle)
                NeptunPermissionProvider.api.permissionBundleService.cachePermissionBundle(permissionBundle.name, permissionBundle)

                permissionBundles.add(permissionBundle)
            }

            permissionBundles
        }
    }

    private suspend fun loadPermissionGroups(permissionBundles: Set<PermissionBundle>): Deferred<Set<PermissionGroup>> =
        withContext(Dispatchers.IO) {
            suspendedTransactionAsync {
                val permissionGroups = mutableSetOf<PermissionGroupImpl>()

                for (permissionGroupEntity in PermissionGroupEntity.all()) {
                    val permissionGroup = PermissionGroupImpl(
                        permissionGroupEntity.name,
                        permissionGroupEntity.priority,
                        TextColor.fromHexString(permissionGroupEntity.color)!!,
                        PermissionApi.defaultGroupFormat,
                        permissionGroupEntity.permissions.map { PermissionInfoImpl(it.permission, it.timeoutTimestamp) }.toMutableSet(),
                        permissionGroupEntity.permissionBundles.map { bundle -> permissionBundles.find { it.name == bundle.name }!! }.toMutableSet(),
                        permissionGroupEntity.parents.map { it.parentName }.toMutableSet(),
                        mutableMapOf(),
                        permissionGroupEntity.isDefault
                    )

                    NeptunStreamlineProvider.api.repositoryLoader.get(PermissionGroupRepository::class.java)?.insert(permissionGroup.name, permissionGroup)
                    NeptunPermissionProvider.api.permissionGroupService.cachePermissionGroup(permissionGroup.name, permissionGroup)

                    permissionGroups.add(permissionGroup)
                }

                permissionGroups
            }
        }

}