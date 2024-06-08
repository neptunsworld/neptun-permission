package world.neptuns.permission.api

import world.neptuns.permission.api.group.PermissionGroupController
import world.neptuns.permission.api.group.PermissionGroupFormat

interface PermissionApi {

    val groupController: PermissionGroupController

    companion object {
        val defaultGroupFormat = PermissionGroupFormat(
            "<groupcolor>[<groupname>] <gray><name>: <white>",
            "<groupcolor>[<groupname>] <gray>",
            "<groupname>"
        )
    }

}