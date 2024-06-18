package world.neptuns.permission.api

import world.neptuns.permission.api.group.PermissionGroupAdapter
import world.neptuns.permission.api.group.PermissionGroupFormat
import world.neptuns.permission.api.group.PermissionGroupService
import world.neptuns.permission.api.permission.PermissionBundleAdapter
import world.neptuns.permission.api.permission.PermissionBundleService
import world.neptuns.permission.api.player.PermissionPlayerAdapter
import world.neptuns.permission.api.player.PermissionPlayerService
import java.util.*

interface PermissionApi {

    val permissionBundleService: PermissionBundleService
    val permissionBundleAdapter: PermissionBundleAdapter

    val permissionGroupService: PermissionGroupService
    val permissionGroupAdapter: PermissionGroupAdapter

    val permissionPlayerService: PermissionPlayerService
    val permissionPlayerAdapter: PermissionPlayerAdapter

    suspend fun sendPlayerPermissionChange(uuid: UUID)

    companion object {
        val defaultGroupFormat = PermissionGroupFormat(
            "<groupcolor><bold><groupname></bold> <gray><name>: <white>",
            "<groupcolor><bold><groupname></bold> <gray>",
            "<groupname>"
        )

        const val PERMANENT_DURATION = -1L
    }

}