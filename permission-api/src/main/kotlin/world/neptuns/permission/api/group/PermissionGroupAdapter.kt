package world.neptuns.permission.api.group

import kotlinx.coroutines.Deferred
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo

interface PermissionGroupAdapter {

    suspend fun getPermissionGroupWithBundle(name: String): Deferred<List<PermissionGroup>>

    suspend fun addPermission(groupName: String, permission: PermissionInfo)
    suspend fun removePermission(groupName: String, permission: String)
    suspend fun removePermissions(groupName: String, permissionNames: List<String>)

    suspend fun addPermissionBundle(groupName: String, permissionBundleInfo: PermissionBundleInfo)
    suspend fun removePermissionBundle(groupName: String, bundleName: String)
    suspend fun removePermissionBundles(groupName: String, bundleNames: List<String>)

    suspend fun addParentGroup(groupName: String, parentGroupName: String)
    suspend fun removeParentGroup(groupName: String, parentGroupName: String)

    suspend fun setDefault(groupName: String, newDefaultState: Boolean)

    suspend fun setMetadata(groupName: String, key: String, value: Any)
    suspend fun removeMetadata(groupName: String, key: String)

    suspend fun deletePermissionGroup(groupName: String)

}