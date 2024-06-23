package world.neptuns.permission.api.group

import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionInfo

interface PermissionGroupAdapter {

    suspend fun addPermission(groupName: String, permission: PermissionInfo)
    suspend fun removePermission(groupName: String, permission: String)

    suspend fun addPermissionBundle(groupName: String, permissionBundle: PermissionBundle)
    suspend fun removePermissionBundle(groupName: String, bundleName: String)

    suspend fun addParentGroup(groupName: String, parentGroupName: String)
    suspend fun removeParentGroup(groupName: String, parentGroupName: String)

    suspend fun setDefault(groupName: String, newDefaultState: Boolean)

    suspend fun setMetadata(groupName: String, key: String, value: Any)
    suspend fun removeMetadata(groupName: String, key: String)

}