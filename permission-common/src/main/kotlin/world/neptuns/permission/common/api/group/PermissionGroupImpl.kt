package world.neptuns.permission.common.api.group

import net.kyori.adventure.text.format.TextColor
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupFormat
import world.neptuns.permission.api.group.PermissionGroupInfo
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo

class PermissionGroupImpl(
    override val name: String,
    override var priority: Int,
    override val color: TextColor,
    override val format: PermissionGroupFormat,
    override var permissions: MutableSet<PermissionInfo>,
    override var permissionBundles: MutableSet<PermissionBundleInfo>,
    override var parents: MutableSet<String>,
    override var metadata: MutableMap<String, Any>,
    override var isDefault: Boolean
) : PermissionGroup {

    override fun collectAllPermissions(): Set<PermissionInfo> {
        TODO("Not yet implemented")
    }

    override fun clearExpiredPermissions() {
        TODO("Not yet implemented")
    }

    override fun hasPermission(permission: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun toGroupInfo(timeoutTimestamp: Long): PermissionGroupInfo {
        return PermissionGroupInfoImpl(this.name, timeoutTimestamp)
    }

}