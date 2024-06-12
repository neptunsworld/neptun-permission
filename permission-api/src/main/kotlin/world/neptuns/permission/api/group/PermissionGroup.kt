package world.neptuns.permission.api.group

import net.kyori.adventure.text.format.TextColor
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionInfo
import java.io.Serializable

interface PermissionGroup : Serializable {

    val name: String
    var priority: Int

    val color: TextColor
    val format: PermissionGroupFormat

    var permissions: MutableSet<PermissionInfo>
    var permissionBundles: MutableSet<PermissionBundle>

    var parents: MutableSet<String>
    var metadata: MutableMap<String, Any>

    var isDefault: Boolean

    fun collectAllPermissions(): Set<PermissionInfo>
    fun clearExpiredPermissions()
    fun hasPermission(permission: String): Boolean

    enum class Update {
        PRIORITY,

        PERMISSION_ADD,
        PERMISSION_REMOVE,
        PERMISSION_CLEAR,

        PARENT_ADD,
        PARENT_REMOVE,
        PARENT_CLEAR,

        METADATA,
        IS_DEFAULT;
    }

}