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

        NeptunPermissionProvider.api.permissionPlayerAdapter.sendPermissionUpdateToPlayersWithBundle(bundleName)
    }

    override suspend fun removePermission(bundleName: String, permission: String) {
        NeptunPermissionProvider.api.permissionBundleService.bulkUpdateEntry(
            updateType = PermissionBundle.Update.PERMISSION_REMOVE,
            key = bundleName,
            newValue = permission,
            updateCache = true
        )

        NeptunPermissionProvider.api.permissionPlayerAdapter.sendPermissionUpdateToPlayersWithBundle(bundleName)
    }

    override suspend fun removePermissions(bundleName: String, permissionNames: List<String>) {
        NeptunPermissionProvider.api.permissionBundleService.bulkUpdateEntry(
            updateType = PermissionBundle.Update.PERMISSION_LIST_REMOVE,
            key = bundleName,
            newValue = permissionNames,
            updateCache = true
        )

        NeptunPermissionProvider.api.permissionPlayerAdapter.sendPermissionUpdateToPlayersWithBundle(bundleName)
    }

    override suspend fun deletePermissionBundle(bundleName: String) {
        NeptunPermissionProvider.api.permissionBundleService.deletePermissionBundle(bundleName)

        for (permissionGroup in NeptunPermissionProvider.api.permissionGroupAdapter.getPermissionGroupWithBundle(bundleName).await()) {
            NeptunPermissionProvider.api.permissionGroupAdapter.removePermissionBundle(permissionGroup.name, bundleName)
        }

        for (permissionPlayer in NeptunPermissionProvider.api.permissionPlayerAdapter.getPermissionPlayersWithBundle(bundleName).await()) {
            NeptunPermissionProvider.api.permissionPlayerAdapter.removePermissionBundle(permissionPlayer.uuid, bundleName)
        }
    }

}