package world.neptuns.permission.common.api.permission

import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionBundleAdapter
import world.neptuns.permission.api.permission.PermissionInfo

class PermissionBundleAdapterImpl : PermissionBundleAdapter {

    override suspend fun addPermission(bundleName: String, permission: PermissionInfo) {
        NeptunPermissionProvider.api.permissionBundleService.bulkUpdateEntry(
            updateType = PermissionBundle.Update.PERMISSION_ADD,
            key = bundleName,
            newValue = permission,
            updateCache = true
        )
    }

    override suspend fun removePermission(bundleName: String, permission: String) {
        NeptunPermissionProvider.api.permissionBundleService.bulkUpdateEntry(
            updateType = PermissionBundle.Update.PERMISSION_REMOVE,
            key = bundleName,
            newValue = permission,
            updateCache = true
        )
    }

}