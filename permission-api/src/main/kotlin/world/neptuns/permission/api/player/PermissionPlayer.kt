package world.neptuns.permission.api.player

import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionInfo
import java.io.Serializable
import java.util.*

interface PermissionPlayer : Serializable {

    val uuid: UUID
    val groups: MutableSet<PermissionGroup>

    val privatePermissionBundles: MutableSet<PermissionBundle>
    val privatePermissions: MutableSet<PermissionInfo>

    var authSession: AuthSession?

    fun clearExpiredPrivatePermissions()
    fun hasPermission(permission: String): Boolean

    fun onActiveGroupExpire()

    enum class Update {
        PERMISSION_BUNDLE_ADD,
        PERMISSION_BUNDLE_REMOVE,
        PERMISSION_BUNDLE_CLEAR,

        PRIVATE_PERMISSION_ADD,
        PRIVATE_PERMISSION_REMOVE,
        PRIVATE_PERMISSION_CLEAR,

        AUTH_SESSION
    }

}