package world.neptuns.permission.api.player

import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupInfo
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo
import java.util.*

interface PermissionPlayerAdapter {

    suspend fun addPermissionGroup(uuid: UUID, group: PermissionGroupInfo)
    suspend fun removePermissionGroup(uuid: UUID, name: String)

    suspend fun addPermissionBundle(uuid: UUID, permissionBundle: PermissionBundleInfo)
    suspend fun removePermissionBundle(uuid: UUID, name: String)

    suspend fun addPrivatePermission(uuid: UUID, permission: PermissionInfo)
    suspend fun removePrivatePermission(uuid: UUID, permission: String)

    suspend fun getHighestPermissionGroup(permissionPlayer: PermissionPlayer): Pair<PermissionGroup, PermissionGroupInfo>?
    suspend fun getPermissionsOfHighestGroup(permissionPlayer: PermissionPlayer): Set<PermissionInfo>

}