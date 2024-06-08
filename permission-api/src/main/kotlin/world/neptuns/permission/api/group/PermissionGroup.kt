package world.neptuns.permission.api.group

import net.kyori.adventure.text.format.TextColor
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.api.utils.Expirable
import java.io.Serializable

interface PermissionGroup : Expirable, Serializable {

    val name: String
    var priority: Int

    val color: TextColor
    val format: PermissionGroupFormat

    val permissions: MutableSet<PermissionInfo>
    val permissionBundles: MutableSet<PermissionBundle>

    val parents: MutableSet<PermissionGroup>
    val metadata: MutableMap<String, Any>

    val isDefault: Boolean

    fun collectAllPermissions(): Set<PermissionInfo>
    fun clearExpiredPermissions()
    fun hasPermission(permission: String): Boolean

    enum class Update {
        ALL,
        PRIORITY,
        PERMISSIONS,
        PARENTS,
        METADATA;
    }

}