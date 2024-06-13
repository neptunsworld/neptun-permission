package world.neptuns.permission.api.group

import net.kyori.adventure.text.format.TextColor
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.streamline.api.utils.Updatable

interface PermissionGroupService : Updatable<String, PermissionGroup.Update, Any> {

    suspend fun getPermissionGroup(name: String): PermissionGroup?
    suspend fun getPermissionGroups(): Set<PermissionGroup>

    suspend fun getDefaultGroup(): PermissionGroup?

    suspend fun createPermissionGroup(
        name: String,
        priority: Int,
        color: TextColor,
        format: PermissionGroupFormat,
        permissions: MutableSet<PermissionInfo>,
        permissionBundles: MutableSet<PermissionBundle>,
        parents: MutableSet<String>,
        isDefault: Boolean,
    )

    suspend fun deletePermissionGroup(name: String)

    suspend fun unloadPermissionGroup(name: String)

    fun cachePermissionGroup(name: String, permissionGroup: PermissionGroup)

    fun removeCachedPermissionGroup(name: String)

}