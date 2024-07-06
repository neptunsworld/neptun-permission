package world.neptuns.permission.api.group

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.format.TextColor
import world.neptuns.permission.api.permission.PermissionBundleInfo
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
        permissionBundles: MutableSet<PermissionBundleInfo>,
        parents: MutableSet<String>,
        isDefault: Boolean,
    )

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun createPermissionGroup(permissionGroup: PermissionGroup) {
        GlobalScope.launch(Dispatchers.IO) {
            createPermissionGroup(
                name = permissionGroup.name,
                priority = permissionGroup.priority,
                color = TextColor.fromHexString(permissionGroup.hexColorCode)!!,
                format = permissionGroup.format,
                permissions = permissionGroup.permissions,
                permissionBundles = permissionGroup.permissionBundles,
                parents = permissionGroup.parents,
                isDefault = permissionGroup.isDefault,
            )
        }
    }

    suspend fun deletePermissionGroup(name: String)

    suspend fun unloadPermissionGroup(name: String)

    fun cachePermissionGroup(name: String, permissionGroup: PermissionGroup)

    fun removeCachedPermissionGroup(name: String)

}