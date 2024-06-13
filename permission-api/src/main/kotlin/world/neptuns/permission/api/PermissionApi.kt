package world.neptuns.permission.api

import world.neptuns.permission.api.group.PermissionGroupFormat
import world.neptuns.permission.api.group.PermissionGroupService
import world.neptuns.permission.api.permission.PermissionBundleService

interface PermissionApi {

    val permissionBundleService: PermissionBundleService
    val permissionGroupService: PermissionGroupService

    companion object {
        val defaultGroupFormat = PermissionGroupFormat(
            "<groupcolor><bold><groupname></bold> <gray><name>: <white>",
            "<groupcolor><bold><groupname></bold> <gray>",
            "<groupname>"
        )
    }

}