package world.neptuns.permission.common.api.permission

import world.neptuns.permission.api.permission.PermissionBundleInfo

class PermissionBundleInfoImpl(override val name: String, override val timeoutTimestamp: Long) : PermissionBundleInfo