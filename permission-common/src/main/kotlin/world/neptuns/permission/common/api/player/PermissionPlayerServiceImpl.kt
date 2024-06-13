package world.neptuns.permission.common.api.player

import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.PermissionApi
import world.neptuns.permission.api.group.PermissionGroupInfo
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.api.player.AuthSession
import world.neptuns.permission.api.player.PermissionPlayer
import world.neptuns.permission.api.player.PermissionPlayerService
import world.neptuns.permission.common.api.permission.PermissionInfoImpl
import world.neptuns.permission.common.database.player.PermissionPlayerGroupInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerPrivatePermissionInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerTable
import world.neptuns.permission.common.database.player.dao.PermissionPlayerEntity
import world.neptuns.permission.common.database.player.dao.PermissionPlayerGroupInfoEntity
import world.neptuns.permission.common.database.player.dao.PermissionPlayerPrivatePermissionInfoEntity
import world.neptuns.permission.common.repository.PermissionPlayerCache
import world.neptuns.permission.common.repository.PermissionPlayerRepository
import world.neptuns.streamline.api.NeptunStreamlineProvider
import java.util.*

@Suppress("OPT_IN_USAGE")
class PermissionPlayerServiceImpl : PermissionPlayerService {

    private val permissionPlayerRepository = NeptunStreamlineProvider.api.repositoryLoader.get(PermissionPlayerRepository::class.java)!!
    private val permissionPlayerCache = NeptunStreamlineProvider.api.cacheLoader.get(PermissionPlayerCache::class.java)!!

    init {
        GlobalScope.launch(Dispatchers.IO) {
            permissionPlayerRepository.onUpdate { uuid, permissionPlayer ->
                if (!permissionPlayerCache.contains(uuid)) return@onUpdate
                permissionPlayerCache.update(uuid, permissionPlayer)
            }
        }
    }

    override suspend fun getPermissionPlayer(uuid: UUID): PermissionPlayer? {
        return this.permissionPlayerCache.get(uuid) ?: this.permissionPlayerRepository.get(uuid).await()
    }

    override suspend fun createPermissionPlayer(uuid: UUID) {
        val defaultGroup = NeptunPermissionProvider.api.permissionGroupService.getDefaultGroup()
            ?: throw NullPointerException("No default group found... Cannot create PermissionPlayer!")

        newSuspendedTransaction {
            PermissionPlayerEntity.new(uuid) {
                this.authSession = null
            }
        }

        val permissionPlayer = PermissionPlayerImpl(
            uuid,
            mutableSetOf(defaultGroup.toGroupInfo(PermissionApi.PERMANENT_DURATION)),
            mutableSetOf(),
            mutableSetOf(),
            null
        )

        cachePermissionPlayer(uuid, permissionPlayer)
        this.permissionPlayerRepository.insert(uuid, permissionPlayer)
    }

    override suspend fun deletePermissionPlayer(uuid: UUID) {
        newSuspendedTransaction {
            PermissionPlayerEntity.find { PermissionPlayerTable.id eq uuid }.singleOrNull()?.delete()
            unloadPermissionPlayer(uuid)
        }
    }

    override suspend fun loadPermissionPlayer(uuid: UUID): Deferred<PermissionPlayer?> = withContext(Dispatchers.IO) {
        suspendedTransactionAsync {
            val existingPermissionPlayer = PermissionPlayerEntity.find { PermissionPlayerTable.id eq uuid }.firstOrNull()

            if (existingPermissionPlayer == null) {
                null
            } else {
                val permissionPlayer = PermissionPlayerImpl(
                    uuid,
                    existingPermissionPlayer.groups.map { group -> NeptunPermissionProvider.api.permissionGroupService.getPermissionGroup(group.groupName)!!.toGroupInfo(group.timeoutTimestamp) }.toMutableSet(),
                    existingPermissionPlayer.privatePermissionBundles.map { bundleInfo -> NeptunPermissionProvider.api.permissionBundleService.getPermissionBundle(bundleInfo.bundleName)!!.toBundleInfo(bundleInfo.timeoutTimestamp) }.toMutableSet(),
                    existingPermissionPlayer.privatePermissions.map { PermissionInfoImpl(it.permission, it.timeoutTimestamp) }.toMutableSet(),
                    if (existingPermissionPlayer.authSession == null) null else AuthSessionImpl(existingPermissionPlayer.authSession!!)
                )

                cachePermissionPlayer(uuid, permissionPlayer)
                permissionPlayerRepository.insert(uuid, permissionPlayer)

                permissionPlayer
            }
        }
    }

    override suspend fun unloadPermissionPlayer(uuid: UUID) {
        this.permissionPlayerRepository.delete(uuid)
        this.permissionPlayerCache.delete(uuid)
    }

    override fun cachePermissionPlayer(uuid: UUID, permissionPlayer: PermissionPlayer) {
        this.permissionPlayerCache.insert(uuid, permissionPlayer)
    }

    override fun removeCachedPermissionPlayer(uuid: UUID) {
        this.permissionPlayerCache.delete(uuid)
    }

    override suspend fun bulkUpdateEntry(updateType: PermissionPlayer.Update, key: UUID, newValue: Any, updateCache: Boolean, result: (Unit) -> Unit) {
        newSuspendedTransaction {
            when (updateType) {
                PermissionPlayer.Update.PERMISSION_GROUP_ADD -> {
                    val permissionGroupInfo = newValue as PermissionGroupInfo

                    PermissionPlayerGroupInfoEntity.new {
                        this.uuid = key
                        this.groupName = permissionGroupInfo.name
                        this.timeoutTimestamp = permissionGroupInfo.timeoutTimestamp
                    }
                }

                PermissionPlayer.Update.PERMISSION_GROUP_REMOVE -> {
                    PermissionPlayerGroupInfoEntity
                        .find { (PermissionPlayerGroupInfoTable.uuid eq key) and (PermissionPlayerGroupInfoTable.groupName eq newValue as String) }
                        .singleOrNull()
                        ?.delete()
                }

                PermissionPlayer.Update.PERMISSION_BUNDLE_ADD -> TODO()
                PermissionPlayer.Update.PERMISSION_BUNDLE_REMOVE -> TODO()
                PermissionPlayer.Update.PERMISSION_BUNDLE_CLEAR -> TODO()

                PermissionPlayer.Update.PRIVATE_PERMISSION_ADD -> {
                    val permissionInfo = newValue as PermissionInfo

                    PermissionPlayerPrivatePermissionInfoEntity.new {
                        this.uuid = key
                        this.permission = permissionInfo.name
                        this.timeoutTimestamp = permissionInfo.timeoutTimestamp
                    }
                }

                PermissionPlayer.Update.PRIVATE_PERMISSION_REMOVE -> {
                    PermissionPlayerPrivatePermissionInfoEntity
                        .find { (PermissionPlayerPrivatePermissionInfoTable.uuid eq key) and (PermissionPlayerPrivatePermissionInfoTable.permission eq newValue as String) }
                        .singleOrNull()
                        ?.delete()
                }

                PermissionPlayer.Update.PRIVATE_PERMISSION_CLEAR -> {
                    PermissionPlayerPrivatePermissionInfoEntity
                        .find { PermissionPlayerPrivatePermissionInfoTable.uuid eq key }
                        .forEach(PermissionPlayerPrivatePermissionInfoEntity::delete)
                }

                PermissionPlayer.Update.AUTH_SESSION -> {
                    val authSession = newValue as AuthSession?

                    PermissionPlayerEntity
                        .find { PermissionPlayerTable.id eq key }
                        .singleOrNull()
                        ?.apply {
                            this.authSession = authSession.toString()
                        }

                    commit()
                }
            }
        }

        if (updateCache) updateCachedEntry(updateType, key, newValue, result)
    }

    override suspend fun updateCachedEntry(updateType: PermissionPlayer.Update, key: UUID, newValue: Any, result: (Unit) -> Unit) {
        val permissionPlayer = getPermissionPlayer(key) ?: return

        when (updateType) {
            PermissionPlayer.Update.PERMISSION_GROUP_ADD -> permissionPlayer.groups.add(newValue as PermissionGroupInfo)
            PermissionPlayer.Update.PERMISSION_GROUP_REMOVE -> permissionPlayer.groups.removeIf { it.name == newValue as String }

            PermissionPlayer.Update.PERMISSION_BUNDLE_ADD -> TODO()
            PermissionPlayer.Update.PERMISSION_BUNDLE_REMOVE -> TODO()
            PermissionPlayer.Update.PERMISSION_BUNDLE_CLEAR -> TODO()

            PermissionPlayer.Update.PRIVATE_PERMISSION_ADD -> permissionPlayer.privatePermissions.add(newValue as PermissionInfo)
            PermissionPlayer.Update.PRIVATE_PERMISSION_REMOVE -> permissionPlayer.privatePermissions.removeIf { it.name == newValue as String }
            PermissionPlayer.Update.PRIVATE_PERMISSION_CLEAR -> permissionPlayer.privatePermissions.clear()

            PermissionPlayer.Update.AUTH_SESSION -> permissionPlayer.authSession = newValue as AuthSession?
        }

        permissionPlayerRepository.update(key, permissionPlayer)
    }

}