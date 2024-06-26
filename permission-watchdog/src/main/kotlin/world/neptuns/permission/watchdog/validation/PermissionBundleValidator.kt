package world.neptuns.permission.watchdog.validation

import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionBundleAdapter
import world.neptuns.permission.api.permission.PermissionBundleService

class PermissionBundleValidator(private val bundleService: PermissionBundleService, private val bundleAdapter: PermissionBundleAdapter) {

    suspend fun validatePermissionBundles() {
        for (permissionBundle in this.bundleService.getPermissionBundles()) {
            checkBundlesForExpiredPermissions(permissionBundle)
        }
    }

    private suspend fun checkBundlesForExpiredPermissions(permissionBundle: PermissionBundle) {
        if (permissionBundle.permissions.none { it.isExpired() }) return
        val expiredPermissions = permissionBundle.permissions.filter { it.isExpired() }

        this.bundleAdapter.removePermissions(permissionBundle.name, expiredPermissions.map { it.name })

    }

}