package world.neptuns.permission.common

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import world.neptuns.permission.api.PermissionApi
import world.neptuns.permission.api.group.PermissionGroupService
import world.neptuns.permission.api.permission.PermissionBundleService
import world.neptuns.permission.api.player.PermissionPlayerService
import world.neptuns.permission.common.api.permission.PermissionBundleServiceImpl
import world.neptuns.permission.common.api.group.PermissionGroupServiceImpl
import world.neptuns.permission.common.api.player.PermissionPlayerServiceImpl
import world.neptuns.permission.common.database.bundle.PermissionBundlePermissionInfoTable
import world.neptuns.permission.common.database.bundle.PermissionBundleTable
import world.neptuns.permission.common.database.group.*
import world.neptuns.permission.common.database.player.PermissionPlayerGroupInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerPrivatePermissionInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerTable
import world.neptuns.permission.common.repository.*
import world.neptuns.streamline.api.NeptunStreamlineProvider

class PermissionApiImpl : PermissionApi {

    override val permissionBundleService: PermissionBundleService = PermissionBundleServiceImpl()
    override val permissionGroupService: PermissionGroupService = PermissionGroupServiceImpl()
    override val permissionPlayerService: PermissionPlayerService = PermissionPlayerServiceImpl()

    init {
        val redissonClient = NeptunStreamlineProvider.api.redissonClient

        val repositoryLoader = NeptunStreamlineProvider.api.repositoryLoader
        repositoryLoader.register(PermissionBundleRepository(redissonClient))
        repositoryLoader.register(PermissionGroupRepository(redissonClient))
        repositoryLoader.register(PermissionPlayerRepository(redissonClient))

        val cacheLoader = NeptunStreamlineProvider.api.cacheLoader
        cacheLoader.register(PermissionBundleCache())
        cacheLoader.register(PermissionGroupCache())
        cacheLoader.register(PermissionPlayerCache())

        transaction {
            SchemaUtils.create(
                // Permission Bundles
                PermissionBundleTable,
                PermissionBundlePermissionInfoTable,
                // Permission Groups
                PermissionGroupTable,
                PermissionGroupPermissionInfoTable,
                PermissionGroupPermissionBundleInfoTable,
                PermissionGroupParentInfoTable,
                PermissionGroupMetadataTable,
                // Permission Players
                PermissionPlayerTable,
                PermissionPlayerGroupInfoTable,
                PermissionPlayerPrivatePermissionInfoTable
            )
        }
    }

    suspend fun loadContents() = PermissionGroupInstanceLoader.load()

}