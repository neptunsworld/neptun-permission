package world.neptuns.permission.api.player

import world.neptuns.permission.api.group.PermissionGroupInfo
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo
import java.io.Serializable
import java.util.*

interface PermissionPlayer : Serializable {

    val uuid: UUID
    val groups: MutableSet<PermissionGroupInfo>

    val privatePermissionBundles: MutableSet<PermissionBundleInfo>
    val privatePermissions: MutableSet<PermissionInfo>

    var authSession: AuthSession?

    suspend fun collectAllPermissions(): Set<PermissionInfo>
    suspend fun clearExpiredPrivatePermissions()
    fun hasPermission(permission: String): Boolean

    suspend fun onActiveGroupExpire()

    enum class Update {
        PERMISSION_GROUP_ADD,
        PERMISSION_GROUP_REMOVE,
        PERMISSION_GROUP_LIST_REMOVE,

        PERMISSION_BUNDLE_ADD,
        PERMISSION_BUNDLE_REMOVE,
        PERMISSION_BUNDLE_CLEAR,
        PERMISSION_BUNDLE_LIST_REMOVE,

        PRIVATE_PERMISSION_ADD,
        PRIVATE_PERMISSION_REMOVE,
        PRIVATE_PERMISSION_CLEAR,
        PRIVATE_PERMISSION_LIST_REMOVE,

        AUTH_SESSION
    }

}