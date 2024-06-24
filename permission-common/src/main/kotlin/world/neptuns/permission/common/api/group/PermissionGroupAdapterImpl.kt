package world.neptuns.permission.common.api.group

import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupAdapter
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo

class PermissionGroupAdapterImpl : PermissionGroupAdapter {

    override suspend fun addPermission(groupName: String, permission: PermissionInfo) {
        NeptunPermissionProvider.api.permissionGroupService.bulkUpdateEntry(
            updateType = PermissionGroup.Update.PERMISSION_ADD,
            key = groupName,
            newValue = permission,
            updateCache = true
        )

        NeptunPermissionProvider.api.permissionPlayerAdapter.sendPermissionUpdateToPlayersWithGroup(groupName)
    }

    override suspend fun removePermission(groupName: String, permission: String) {
        NeptunPermissionProvider.api.permissionGroupService.bulkUpdateEntry(
            updateType = PermissionGroup.Update.PERMISSION_REMOVE,
            key = groupName,
            newValue = permission,
            updateCache = true
        )

        NeptunPermissionProvider.api.permissionPlayerAdapter.sendPermissionUpdateToPlayersWithGroup(groupName)
    }

    override suspend fun addPermissionBundle(groupName: String, permissionBundleInfo: PermissionBundleInfo) {
        NeptunPermissionProvider.api.permissionGroupService.bulkUpdateEntry(
            updateType = PermissionGroup.Update.PERMISSION_BUNDLE_ADD,
            key = groupName,
            newValue = permissionBundleInfo,
            updateCache = true
        )

        NeptunPermissionProvider.api.permissionPlayerAdapter.sendPermissionUpdateToPlayersWithGroup(groupName)
    }

    override suspend fun removePermissionBundle(groupName: String, bundleName: String) {
        NeptunPermissionProvider.api.permissionGroupService.bulkUpdateEntry(
            updateType = PermissionGroup.Update.PERMISSION_BUNDLE_REMOVE,
            key = groupName,
            newValue = bundleName,
            updateCache = true
        )

        NeptunPermissionProvider.api.permissionPlayerAdapter.sendPermissionUpdateToPlayersWithGroup(groupName)
    }

    override suspend fun addParentGroup(groupName: String, parentGroupName: String) {
        NeptunPermissionProvider.api.permissionGroupService.bulkUpdateEntry(
            updateType = PermissionGroup.Update.PARENT_ADD,
            key = groupName,
            newValue = parentGroupName,
            updateCache = true
        )

        NeptunPermissionProvider.api.permissionPlayerAdapter.sendPermissionUpdateToPlayersWithGroup(groupName)
    }

    override suspend fun removeParentGroup(groupName: String, parentGroupName: String) {
        NeptunPermissionProvider.api.permissionGroupService.bulkUpdateEntry(
            updateType = PermissionGroup.Update.PARENT_REMOVE,
            key = groupName,
            newValue = parentGroupName,
            updateCache = true
        )

        NeptunPermissionProvider.api.permissionPlayerAdapter.sendPermissionUpdateToPlayersWithGroup(groupName)
    }

    override suspend fun setDefault(groupName: String, newDefaultState: Boolean) {
        val groupService = NeptunPermissionProvider.api.permissionGroupService

        val defaultGroup = groupService.getDefaultGroup()

        if (defaultGroup != null && newDefaultState) {
            groupService.bulkUpdateEntry(
                updateType = PermissionGroup.Update.IS_DEFAULT,
                key = defaultGroup.name,
                newValue = false,
                updateCache = true
            )
        }

        groupService.bulkUpdateEntry(
            updateType = PermissionGroup.Update.IS_DEFAULT,
            key = groupName,
            newValue = newDefaultState,
            updateCache = true
        )
    }

    override suspend fun setMetadata(groupName: String, key: String, value: Any) {
        NeptunPermissionProvider.api.permissionGroupService.bulkUpdateEntry(
            updateType = PermissionGroup.Update.METADATA_SET,
            key = groupName,
            newValue = Pair(key, value),
            updateCache = true
        )
    }

    override suspend fun removeMetadata(groupName: String, key: String) {
        NeptunPermissionProvider.api.permissionGroupService.bulkUpdateEntry(
            updateType = PermissionGroup.Update.METADATA_SET,
            key = groupName,
            newValue = key,
            updateCache = true
        )
    }

}