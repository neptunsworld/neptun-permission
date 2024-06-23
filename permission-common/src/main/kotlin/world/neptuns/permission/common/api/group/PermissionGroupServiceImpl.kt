package world.neptuns.permission.common.api.group

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.format.TextColor
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupFormat
import world.neptuns.permission.api.group.PermissionGroupService
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.common.database.group.*
import world.neptuns.permission.common.database.group.dao.*
import world.neptuns.permission.common.repository.PermissionGroupCache
import world.neptuns.permission.common.repository.PermissionGroupRepository
import world.neptuns.streamline.api.NeptunStreamlineProvider
import world.neptuns.streamline.api.StreamlineApi

@Suppress("OPT_IN_USAGE")
class PermissionGroupServiceImpl : PermissionGroupService {

    private val permissionGroupRepository = NeptunStreamlineProvider.api.repositoryLoader.get(PermissionGroupRepository::class.java)!!
    private val permissionGroupCache = NeptunStreamlineProvider.api.cacheLoader.get(PermissionGroupCache::class.java)!!

    init {
        GlobalScope.launch(Dispatchers.IO) {
            permissionGroupRepository.onInsertion { name, permissionGroup ->
                if (permissionGroupCache.contains(name)) return@onInsertion
                permissionGroupCache.insert(name, permissionGroup)
            }

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
        permissionBundles: MutableSet<PermissionBundleInfo>,
        parents: MutableSet<String>,
        isDefault: Boolean,
    ) {
        newSuspendedTransaction {
            PermissionGroupEntity.new {
                this.name = name
                this.priority = priority
                this.color = color.asHexString()
                this.format = StreamlineApi.GSON.toJson(format)
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
                PermissionGroupPermissionBundleInfoEntity.new {
                    this.groupName = name
                    this.name = permissionBundle.name
                    this.timeoutTimestamp = permissionBundle.timeoutTimestamp
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
            color.asHexString(),
            format,
            permissions,
            permissionBundles,
            parents,
            mutableMapOf(),
            isDefault
        )

        cachePermissionGroup(name, permissionGroup)
        this.permissionGroupRepository.insert(name, permissionGroup)
    }

    override suspend fun deletePermissionGroup(name: String) {
        newSuspendedTransaction {
            PermissionGroupEntity.find { PermissionGroupTable.name eq name }.singleOrNull()?.delete()
            unloadPermissionGroup(name)
        }
    }

//    override suspend fun loadPermissionGroup(name: String): Deferred<PermissionGroup?> = withContext(Dispatchers.IO) {
//        suspendedTransactionAsync {
//            val existingPermissionGroupEntity = PermissionGroupEntity.find { PermissionGroupTable.name eq name }.firstOrNull()
//
//            if (existingPermissionGroupEntity == null) {
//                null
//            } else {
//                val permissionGroup = PermissionGroupImpl(
//                    name,
//                    existingPermissionGroupEntity.priority,
//                    TextColor.fromHexString(existingPermissionGroupEntity.color)!!,
//                    StreamlineApi.GSON.fromJson(existingPermissionGroupEntity.format, PermissionGroupFormat::class.java),
//                    existingPermissionGroupEntity.permissions.map { PermissionInfoImpl(it.permission, it.timeoutTimestamp) }.toMutableSet(),
//                    existingPermissionGroupEntity.permissionBundles.map { bundle -> NeptunPermissionProvider.api.permissionBundleService.getPermissionBundle(bundle.name)!! }.toMutableSet(),
//                    existingPermissionGroupEntity.parents.map { it.parentName }.toMutableSet(),
//                    existingPermissionGroupEntity.metadata.associate { Pair(it.key, it.value) }.toMutableMap(),
//                    existingPermissionGroupEntity.isDefault
//                )
//
//                cachePermissionGroup(name, permissionGroup)
//                permissionGroupRepository.insert(name, permissionGroup)
//
//                permissionGroup
//            }
//        }
//    }

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

    @Suppress("UNCHECKED_CAST")
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
                    val permissionInfo = newValue as PermissionInfo

                    PermissionGroupPermissionInfoEntity.new {
                        this.groupName = key
                        this.permission = permissionInfo.name
                        this.timeoutTimestamp = permissionInfo.timeoutTimestamp
                    }
                }

                PermissionGroup.Update.PERMISSION_REMOVE -> {
                    PermissionGroupPermissionInfoEntity
                        .find { PermissionGroupPermissionInfoTable.permission eq newValue as String }
                        .singleOrNull()
                        ?.delete()
                }

                PermissionGroup.Update.PERMISSION_CLEAR -> {
                    PermissionGroupPermissionInfoEntity
                        .find { PermissionGroupPermissionInfoTable.groupName eq key }
                        .forEach(PermissionGroupPermissionInfoEntity::delete)
                }

                PermissionGroup.Update.PERMISSION_BUNDLE_ADD -> {
                    val permissionBundleInfo = newValue as PermissionBundleInfo

                    PermissionGroupPermissionBundleInfoEntity.new {
                        this.groupName = key
                        this.name = permissionBundleInfo.name
                        this.timeoutTimestamp = permissionBundleInfo.timeoutTimestamp
                    }
                }

                PermissionGroup.Update.PERMISSION_BUNDLE_REMOVE -> {
                    PermissionGroupPermissionBundleInfoEntity
                        .find { (PermissionGroupPermissionBundleInfoTable.groupName eq key) and (PermissionGroupPermissionBundleInfoTable.bundleName eq newValue as String) }
                        .singleOrNull()
                        ?.delete()
                }

                PermissionGroup.Update.PERMISSION_BUNDLE_CLEAR -> {
                    PermissionGroupPermissionBundleInfoEntity
                        .find { PermissionGroupPermissionBundleInfoTable.groupName eq key }
                        .firstOrNull()
                        ?.delete()
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
                    PermissionGroupParentInfoEntity
                        .find { PermissionGroupParentInfoTable.groupName eq key }
                        .forEach(PermissionGroupParentInfoEntity::delete)
                }

                PermissionGroup.Update.METADATA_SET -> {
                    val pair = newValue as Pair<String, Any>
                    PermissionGroupMetadataEntity.new {
                        this.groupName = key
                        this.key = pair.first
                        this.value = StreamlineApi.GSON.toJson(pair.second)
                    }
                }

                PermissionGroup.Update.METADATA_REMOVE -> {
                    PermissionGroupMetadataEntity
                        .find { (PermissionGroupMetadataTable.groupName eq key) and (PermissionGroupMetadataTable.key eq newValue as String) }
                        .singleOrNull()
                        ?.delete()
                }

                PermissionGroup.Update.METADATA_CLEAR -> {
                    PermissionGroupMetadataEntity.find { PermissionGroupMetadataTable.groupName eq key }.forEach(PermissionGroupMetadataEntity::delete)
                }

                PermissionGroup.Update.IS_DEFAULT -> {
                    groupEntity.apply { this.isDefault = newValue as Boolean }
                    commit()
                }
            }
        }

        if (updateCache) updateCachedEntry(updateType, key, newValue, result)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun updateCachedEntry(updateType: PermissionGroup.Update, key: String, newValue: Any, result: (Unit) -> Unit) {
        val permissionGroup = getPermissionGroup(key) ?: return

        when (updateType) {
            PermissionGroup.Update.PRIORITY -> permissionGroup.priority = newValue as Int

            PermissionGroup.Update.PERMISSION_ADD -> permissionGroup.permissions.add(newValue as PermissionInfo)
            PermissionGroup.Update.PERMISSION_REMOVE -> permissionGroup.permissions.removeIf { it.name == newValue as String }
            PermissionGroup.Update.PERMISSION_CLEAR -> permissionGroup.permissions.clear()

            PermissionGroup.Update.PERMISSION_BUNDLE_ADD -> permissionGroup.permissionBundles.add(newValue as PermissionBundleInfo)
            PermissionGroup.Update.PERMISSION_BUNDLE_REMOVE -> permissionGroup.permissionBundles.removeIf { it.name == newValue as String }
            PermissionGroup.Update.PERMISSION_BUNDLE_CLEAR -> permissionGroup.permissionBundles.clear()

            PermissionGroup.Update.PARENT_ADD -> permissionGroup.parents.add(newValue as String)
            PermissionGroup.Update.PARENT_REMOVE -> permissionGroup.parents.remove(newValue as String)
            PermissionGroup.Update.PARENT_CLEAR -> permissionGroup.parents.clear()

            PermissionGroup.Update.METADATA_SET -> {
                val pair = newValue as Pair<String, Any>
                permissionGroup.metadata[pair.first] = pair.second
            }

            PermissionGroup.Update.METADATA_REMOVE -> permissionGroup.metadata.remove(newValue as String)
            PermissionGroup.Update.METADATA_CLEAR -> permissionGroup.metadata.clear()
            PermissionGroup.Update.IS_DEFAULT -> permissionGroup.isDefault = newValue as Boolean
        }

        permissionGroupRepository.update(key, permissionGroup)
    }

}