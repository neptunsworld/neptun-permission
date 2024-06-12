package world.neptuns.permission.common

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import world.neptuns.permission.api.PermissionApi
import world.neptuns.permission.api.group.PermissionGroupService
import world.neptuns.permission.api.permission.PermissionBundleService
import world.neptuns.permission.common.api.bundle.PermissionBundleServiceImpl
import world.neptuns.permission.common.api.group.PermissionGroupServiceImpl
import world.neptuns.permission.common.database.bundle.PermissionBundlePermissionInfoTable
import world.neptuns.permission.common.database.bundle.PermissionBundleTable
import world.neptuns.permission.common.database.group.PermissionGroupParentInfoTable
import world.neptuns.permission.common.database.group.PermissionGroupPermissionInfoTable
import world.neptuns.permission.common.database.group.PermissionGroupTable
import world.neptuns.permission.common.database.player.PermissionPlayerGroupInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerPrivatePermissionInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerTable
import world.neptuns.permission.common.repository.PermissionBundleCache
import world.neptuns.permission.common.repository.PermissionBundleRepository
import world.neptuns.permission.common.repository.PermissionGroupCache
import world.neptuns.permission.common.repository.PermissionGroupRepository
import world.neptuns.streamline.api.NeptunStreamlineProvider

class PermissionApiImpl : PermissionApi {

    override val permissionBundleService: PermissionBundleService = PermissionBundleServiceImpl()
    override val permissionGroupService: PermissionGroupService = PermissionGroupServiceImpl()

    init {
        val redissonClient = NeptunStreamlineProvider.api.redissonClient

        val repositoryLoader = NeptunStreamlineProvider.api.repositoryLoader
        repositoryLoader.register(PermissionBundleRepository(redissonClient))
        repositoryLoader.register(PermissionGroupRepository(redissonClient))

        val cacheLoader = NeptunStreamlineProvider.api.cacheLoader
        cacheLoader.register(PermissionBundleCache())
        cacheLoader.register(PermissionGroupCache())

        transaction {
            SchemaUtils.create(
                // Permission Bundles
                PermissionBundleTable,
                PermissionBundlePermissionInfoTable,
                // Permission Groups
                PermissionGroupTable,
                PermissionGroupParentInfoTable,
                PermissionGroupPermissionInfoTable,
                // Permission Players
                PermissionPlayerTable,
                PermissionPlayerGroupInfoTable,
                PermissionPlayerPrivatePermissionInfoTable
            )
        }
    }

    suspend fun loadContents() = PermissionGroupInstanceLoader.load()

}