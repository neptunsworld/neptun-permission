package world.neptuns.permission.common

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import world.neptuns.permission.api.PermissionApi
import world.neptuns.permission.api.group.PermissionGroupAdapter
import world.neptuns.permission.api.group.PermissionGroupService
import world.neptuns.permission.api.permission.PermissionBundleAdapter
import world.neptuns.permission.api.permission.PermissionBundleService
import world.neptuns.permission.api.player.PermissionPlayerAdapter
import world.neptuns.permission.api.player.PermissionPlayerService
import world.neptuns.permission.common.api.group.PermissionGroupAdapterImpl
import world.neptuns.permission.common.api.group.PermissionGroupServiceImpl
import world.neptuns.permission.common.api.permission.PermissionBundleAdapterImpl
import world.neptuns.permission.common.api.permission.PermissionBundleServiceImpl
import world.neptuns.permission.common.api.player.PermissionPlayerAdapterImpl
import world.neptuns.permission.common.api.player.PermissionPlayerServiceImpl
import world.neptuns.permission.common.database.bundle.PermissionBundlePermissionInfoTable
import world.neptuns.permission.common.database.bundle.PermissionBundleTable
import world.neptuns.permission.common.database.group.*
import world.neptuns.permission.common.database.player.PermissionPlayerGroupInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerPrivatePermissionBundleInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerPrivatePermissionInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerTable
import world.neptuns.permission.common.repository.*
import world.neptuns.streamline.api.NeptunStreamlineProvider

class PermissionApiImpl : PermissionApi {

    override val permissionBundleService: PermissionBundleService
    override val permissionBundleAdapter: PermissionBundleAdapter

    override val permissionGroupService: PermissionGroupService
    override val permissionGroupAdapter: PermissionGroupAdapter

    override val permissionPlayerService: PermissionPlayerService
    override val permissionPlayerAdapter: PermissionPlayerAdapter

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
                PermissionPlayerPrivatePermissionBundleInfoTable,
                PermissionPlayerPrivatePermissionInfoTable
            )
        }

        this.permissionBundleService = PermissionBundleServiceImpl()
        this.permissionBundleAdapter = PermissionBundleAdapterImpl()

        this.permissionGroupService = PermissionGroupServiceImpl()
        this.permissionGroupAdapter = PermissionGroupAdapterImpl()

        this.permissionPlayerService = PermissionPlayerServiceImpl()
        this.permissionPlayerAdapter = PermissionPlayerAdapterImpl()
    }

    suspend fun loadContents() = PermissionGroupInstanceLoader.load()

}