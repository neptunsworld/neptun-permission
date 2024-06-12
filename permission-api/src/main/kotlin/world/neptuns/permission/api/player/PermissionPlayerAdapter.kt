package world.neptuns.permission.api.player

import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.permission.PermissionInfo

interface PermissionPlayerAdapter {

    suspend fun addGroup(permissionPlayer: PermissionPlayer, group: PermissionGroup)
    suspend fun removeGroup(permissionPlayer: PermissionPlayer, groupName: String)

    suspend fun addPrivatePermission(permissionPlayer: PermissionPlayer, permission: PermissionInfo)
    suspend fun removePrivatePermission(permissionPlayer: PermissionPlayer, permission: String)

    suspend fun getPermissionsOfHighestGroup(permissionPlayer: PermissionPlayer): Set<PermissionInfo>

//    suspend fun getPermissionsOfHighestGroup(permissionPlayer: PermissionPlayer): Set<PermissionInfo> {
//        // lowest priority = most powerful group
//        return permissionPlayer.groups.minByOrNull { it.priority }?.collectAllPermissions() ?: emptySet()
//    }

}