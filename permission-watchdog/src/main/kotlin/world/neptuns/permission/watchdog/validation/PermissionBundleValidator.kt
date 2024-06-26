package world.neptuns.permission.watchdog.validation

import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionBundleAdapter
import world.neptuns.permission.api.permission.PermissionBundleService
import java.util.logging.Logger

class PermissionBundleValidator(private val bundleService: PermissionBundleService, private val bundleAdapter: PermissionBundleAdapter) {

    suspend fun validatePermissionBundles() {
        for (permissionBundle in this.bundleService.getPermissionBundles()) {
            checkBundlesForExpiredPermissions(permissionBundle)
        }
    }

    private suspend fun checkBundlesForExpiredPermissions(permissionBundle: PermissionBundle) {
        val expiredPermissions = permissionBundle.permissions.filter { it.isExpired() }
        if (expiredPermissions.isEmpty()) return

        Logger.getGlobal().info("The following permission have been removed from bundle ${permissionBundle.name}")
        this.bundleAdapter.removePermissions(permissionBundle.name, expiredPermissions.map { it.name })
    }

}