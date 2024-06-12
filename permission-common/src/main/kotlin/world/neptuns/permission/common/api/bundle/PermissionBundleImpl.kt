package world.neptuns.permission.common.api.bundle

import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionInfo

class PermissionBundleImpl(
    override val name: String,
    override val permissions: MutableSet<PermissionInfo>,
    override val isAuthRequired: Boolean,
    override val timeoutTimestamp: Long,
) : PermissionBundle