package world.neptuns.permission.common.api.permission

import world.neptuns.permission.api.permission.PermissionInfo

class PermissionInfoImpl(override val name: String, override val timeoutTimestamp: Long) : PermissionInfo