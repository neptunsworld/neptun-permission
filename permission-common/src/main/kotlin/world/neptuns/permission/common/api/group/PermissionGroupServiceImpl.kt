package world.neptuns.permission.common.api.group

import kotlinx.coroutines.*
import net.kyori.adventure.text.format.TextColor
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import world.neptuns.core.base.api.CoreBaseApi
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupFormat
import world.neptuns.permission.api.group.PermissionGroupService
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.common.api.permission.PermissionInfoImpl
import world.neptuns.permission.common.database.group.PermissionGroupParentInfoTable
import world.neptuns.permission.common.database.group.PermissionGroupPermissionInfoTable
import world.neptuns.permission.common.database.group.PermissionGroupTable
import world.neptuns.permission.common.database.group.dao.PermissionGroupBundleInfoEntity
import world.neptuns.permission.common.database.group.dao.PermissionGroupEntity
import world.neptuns.permission.common.database.group.dao.PermissionGroupParentInfoEntity
import world.neptuns.permission.common.database.group.dao.PermissionGroupPermissionInfoEntity
import world.neptuns.permission.common.repository.PermissionGroupCache
import world.neptuns.permission.common.repository.PermissionGroupRepository
import world.neptuns.streamline.api.NeptunStreamlineProvider

@Suppress("OPT_IN_USAGE")
class PermissionGroupServiceImpl : PermissionGroupService {

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
        return this.permissionGroupCache.get(name) ?: this.permissionGroupRepository.get(name).await()
    }

    override suspend fun getPermissionGroups(): Set<PermissionGroup> {
        return this.permissionGroupCache.getAll().toSet()
    }

    override suspend fun getDefaultGroup(): PermissionGroup? {
        return this.permissionGroupCache
            .getAll { it.isDefault }.singleOrNull()
            ?: this.permissionGroupRepository.getAll { it.isDefault }.await().firstOrNull()
    }

    override suspend fun createPermissionGroup(
        name: String,
        priority: Int,
        color: TextColor,
        format: PermissionGroupFormat,
        permissions: MutableSet<PermissionInfo>,
        permissionBundles: MutableSet<PermissionBundle>,
        parents: MutableSet<String>,
        isDefault: Boolean,
    ) {
        newSuspendedTransaction {
            PermissionGroupEntity.new {
                this.name = name
                this.priority = priority
                this.color = color.asHexString()
                this.format = CoreBaseApi.GSON.toJson(format)
                this.isDefault = isDefault
            }

            permissions.forEach { permission ->
                PermissionGroupPermissionInfoEntity.new {
                    this.groupName = name
                    this.permission = permission.name
                    this.timeoutTimestamp = permission.timeoutTimestamp
                }
            }

            permissionBundles.forEach { permissionBundle ->
                PermissionGroupBundleInfoEntity.new {
                    this.groupName = name
                    this.name = permissionBundle.name
                }
            }

            parents.forEach { parentGroupName ->
                PermissionGroupParentInfoEntity.new {
                    this.groupName = name
                    this.parentName = parentGroupName
                }
            }

        }

        val permissionGroup = PermissionGroupImpl(
            name,
            priority,
            color,
            format,
            permissions,
            permissionBundles,
            parents,
            mutableMapOf(),
            isDefault
        )

        cachePermissionGroup(name, permissionGroup)
        permissionGroupRepository.insert(name, permissionGroup)
    }

    override suspend fun deletePermissionGroup(name: String) {
        newSuspendedTransaction {
            val groupEntity = PermissionGroupEntity
                .find { PermissionGroupTable.name eq name }
                .singleOrNull() ?: return@newSuspendedTransaction

            groupEntity.delete()
            unloadPermissionGroup(name)
        }
    }

    override suspend fun loadPermissionGroup(name: String): Deferred<PermissionGroup?> = withContext(Dispatchers.IO) {
        suspendedTransactionAsync {
            val existingPermissionGroupEntity = PermissionGroupEntity.find { PermissionGroupTable.name eq name }.firstOrNull()

            if (existingPermissionGroupEntity == null) {
                null
            } else {
                val permissionGroup = PermissionGroupImpl(
                    name,
                    existingPermissionGroupEntity.priority,
                    TextColor.fromHexString(existingPermissionGroupEntity.color)!!,
                    CoreBaseApi.GSON.fromJson(existingPermissionGroupEntity.format, PermissionGroupFormat::class.java),
                    existingPermissionGroupEntity.permissions.map { PermissionInfoImpl(it.permission, it.timeoutTimestamp) }.toMutableSet(),
                    existingPermissionGroupEntity.permissionBundles.map { bundle -> NeptunPermissionProvider.api.permissionBundleService.getPermissionBundle(bundle.name)!! }.toMutableSet(),
                    existingPermissionGroupEntity.parents.map { it.parentName }.toMutableSet(),
                    mutableMapOf(),
                    existingPermissionGroupEntity.isDefault
                )

                cachePermissionGroup(name, permissionGroup)
                permissionGroupRepository.insert(name, permissionGroup)

                permissionGroup
            }
        }
    }

    override suspend fun unloadPermissionGroup(name: String) {
        this.permissionGroupRepository.delete(name)
        this.permissionGroupCache.delete(name)
    }

    override fun cachePermissionGroup(name: String, permissionGroup: PermissionGroup) {
        this.permissionGroupCache.insert(name, permissionGroup)
    }

    override fun removeCachedPermissionGroup(name: String) {
        this.permissionGroupCache.delete(name)
    }

    override suspend fun bulkUpdateEntry(updateType: PermissionGroup.Update, key: String, newValue: Any, updateCache: Boolean, result: (Unit) -> Unit) {
        newSuspendedTransaction {
            val groupEntity = PermissionGroupEntity
                .find { PermissionGroupTable.name eq key }
                .singleOrNull() ?: return@newSuspendedTransaction

            when (updateType) {
                PermissionGroup.Update.PRIORITY -> {
                    groupEntity.apply { this.priority = newValue as Int }
                    commit()
                }

                PermissionGroup.Update.PERMISSION_ADD -> {
                    val permissionInfo = newValue as PermissionInfoImpl

                    PermissionGroupPermissionInfoEntity.new {
                        this.groupName = groupEntity.name
                        this.permission = permissionInfo.name
                        this.timeoutTimestamp = permissionInfo.timeoutTimestamp
                    }
                }

                PermissionGroup.Update.PERMISSION_REMOVE -> {
                    val permissionInfo = newValue as PermissionInfoImpl
                    PermissionGroupPermissionInfoEntity
                        .find { PermissionGroupPermissionInfoTable.permission eq permissionInfo.name }
                        .singleOrNull()
                        ?.delete()
                }

                PermissionGroup.Update.PERMISSION_CLEAR -> {
                    PermissionGroupPermissionInfoEntity.find { PermissionGroupPermissionInfoTable.groupName eq key }.forEach(PermissionGroupPermissionInfoEntity::delete)
                }

                PermissionGroup.Update.PARENT_ADD -> {
                    PermissionGroupParentInfoEntity.new {
                        this.groupName = key
                        this.parentName = newValue as String
                    }
                }

                PermissionGroup.Update.PARENT_REMOVE -> {
                    PermissionGroupParentInfoEntity
                        .find { (PermissionGroupParentInfoTable.groupName eq key) and (PermissionGroupParentInfoTable.parentName eq newValue as String) }
                        .singleOrNull()
                        ?.delete()
                }

                PermissionGroup.Update.PARENT_CLEAR -> {
                    PermissionGroupParentInfoEntity.find { PermissionGroupParentInfoTable.groupName eq key }.forEach(PermissionGroupParentInfoEntity::delete)
                }

                PermissionGroup.Update.IS_DEFAULT -> {
                    groupEntity.apply { this.isDefault = newValue as Boolean }
                    commit()
                }

                PermissionGroup.Update.METADATA -> TODO()
            }
        }

        if (updateCache) updateCachedEntry(updateType, key, newValue, result)
    }

    override suspend fun updateCachedEntry(updateType: PermissionGroup.Update, key: String, newValue: Any, result: (Unit) -> Unit) {
        val permissionGroup = getPermissionGroup(key) ?: return

        when (updateType) {
            PermissionGroup.Update.PRIORITY -> permissionGroup.priority = newValue as Int
            PermissionGroup.Update.PERMISSION_ADD -> permissionGroup.permissions.add(newValue as PermissionInfo)
            PermissionGroup.Update.PERMISSION_REMOVE -> permissionGroup.permissions.removeIf { it.name == newValue as String }
            PermissionGroup.Update.PERMISSION_CLEAR -> permissionGroup.permissions.clear()
            PermissionGroup.Update.PARENT_ADD -> permissionGroup.parents.add(newValue as String)
            PermissionGroup.Update.PARENT_REMOVE -> permissionGroup.parents.remove(newValue as String)
            PermissionGroup.Update.PARENT_CLEAR -> permissionGroup.parents.clear()
            PermissionGroup.Update.METADATA -> TODO()
            PermissionGroup.Update.IS_DEFAULT -> permissionGroup.isDefault = newValue as Boolean
        }

        permissionGroupRepository.update(key, permissionGroup)
    }

}