package world.neptuns.permission.common.api.group

import world.neptuns.permission.api.group.PermissionGroupInfo

class PermissionGroupInfoImpl(override val name: String, override val timeoutTimestamp: Long) : PermissionGroupInfo