package world.neptuns.permission.common.api.permission

import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo

class PermissionBundleImpl(
    override val name: String,
    override val permissions: MutableSet<PermissionInfo>,
    override var isAuthRequired: Boolean
) : PermissionBundle {

    override fun toBundleInfo(timeoutTimestamp: Long): PermissionBundleInfo {
        return PermissionBundleInfoImpl(this.name, timeoutTimestamp)
    }

}