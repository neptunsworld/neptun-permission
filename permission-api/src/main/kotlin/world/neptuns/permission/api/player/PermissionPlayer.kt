package world.neptuns.permission.api.player

import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionInfo
import java.io.Serializable
import java.util.UUID

interface PermissionPlayer : Serializable {

    val uuid: UUID
    val groups: MutableSet<PermissionGroup>

    val privatePermissionBundles: MutableSet<PermissionBundle>
    val privatePermissions: MutableSet<PermissionInfo>

    var authSession: AuthSession?

    fun clearExpiredPrivatePermissions()
    fun hasPermission(permission: String): Boolean

    fun onActiveGroupExpire()

}