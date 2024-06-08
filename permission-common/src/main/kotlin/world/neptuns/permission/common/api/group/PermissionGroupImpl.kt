package world.neptuns.permission.common.api.group

import net.kyori.adventure.text.format.TextColor
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupFormat
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionInfo

class PermissionGroupImpl(
    override val name: String,
    override var priority: Int,
    override val color: TextColor,
    override val format: PermissionGroupFormat,
    override val permissions: MutableSet<PermissionInfo>,
    override val permissionBundles: MutableSet<PermissionBundle>,
    override val parents: MutableSet<PermissionGroup>,
    override val metadata: MutableMap<String, Any>,
    override val isDefault: Boolean,
    override val timeoutTimestamp: Long
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

}