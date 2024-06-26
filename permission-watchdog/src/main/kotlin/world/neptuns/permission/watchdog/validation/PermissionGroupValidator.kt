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
        val expiredPermissions = permissionGroup.permissions.filter { it.isExpired() }
        if (expiredPermissions.isEmpty()) return

        this.groupAdapter.removePermissions(permissionGroup.name, expiredPermissions.map { it.name })
    }

    private suspend fun checkGroupsForExpiredBundles(permissionGroup: PermissionGroup) {
        val expiredBundles = permissionGroup.permissionBundles.filter { it.isExpired() }
        if (expiredBundles.isEmpty()) return

        this.groupAdapter.removePermissionBundles(permissionGroup.name, expiredBundles.map { it.name })
    }

}