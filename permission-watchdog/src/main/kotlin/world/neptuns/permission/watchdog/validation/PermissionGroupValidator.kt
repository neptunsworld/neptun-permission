package world.neptuns.permission.watchdog.validation

import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupAdapter
import world.neptuns.permission.api.group.PermissionGroupService

class PermissionGroupValidator(private val groupService: PermissionGroupService, private val groupAdapter: PermissionGroupAdapter) {

    suspend fun validatePermissionGroups() {
        for (permissionGroup in this.groupService.getPermissionGroups()) {
            checkGroupsForExpiredPermissions(permissionGroup)
            checkGroupsForExpiredBundles(permissionGroup)
        }
    }

    private suspend fun checkGroupsForExpiredPermissions(permissionGroup: PermissionGroup) {
        if (permissionGroup.permissions.none { it.isExpired() }) return
        val expiredPermissions = permissionGroup.permissions.filter { it.isExpired() }

        this.groupAdapter.removePermissions(permissionGroup.name, expiredPermissions.map { it.name })
    }

    private suspend fun checkGroupsForExpiredBundles(permissionGroup: PermissionGroup) {
        if (permissionGroup.permissionBundles.none { it.isExpired() }) return
        val expiredBundles = permissionGroup.permissionBundles.filter { it.isExpired() }

        this.groupAdapter.removePermissionBundles(permissionGroup.name, expiredBundles.map { it.name })
    }

}