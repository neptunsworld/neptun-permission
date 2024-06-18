package world.neptuns.permission.common.api.player

import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupInfo
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.api.player.PermissionPlayer
import world.neptuns.permission.api.player.PermissionPlayerAdapter
import java.util.*

class PermissionPlayerAdapterImpl : PermissionPlayerAdapter {

    override suspend fun addPermissionGroup(uuid: UUID, group: PermissionGroupInfo) {
        NeptunPermissionProvider.api.permissionPlayerService.bulkUpdateEntry(
            updateType = PermissionPlayer.Update.PERMISSION_GROUP_ADD,
            key = uuid,
            newValue = group,
            updateCache = true
        )

        NeptunPermissionProvider.api.sendPlayerPermissionChange(uuid)
    }

    override suspend fun removePermissionGroup(uuid: UUID, name: String) {
        NeptunPermissionProvider.api.permissionPlayerService.bulkUpdateEntry(
            updateType = PermissionPlayer.Update.PERMISSION_GROUP_REMOVE,
            key = uuid,
            newValue = name,
            updateCache = true
        )

        NeptunPermissionProvider.api.sendPlayerPermissionChange(uuid)
    }

    override suspend fun addPermissionBundle(uuid: UUID, permissionBundle: PermissionBundleInfo) {
        NeptunPermissionProvider.api.permissionPlayerService.bulkUpdateEntry(
            updateType = PermissionPlayer.Update.PERMISSION_BUNDLE_ADD,
            key = uuid,
            newValue = permissionBundle,
            updateCache = true
        )

        NeptunPermissionProvider.api.sendPlayerPermissionChange(uuid)
    }

    override suspend fun removePermissionBundle(uuid: UUID, name: String) {
        NeptunPermissionProvider.api.permissionPlayerService.bulkUpdateEntry(
            updateType = PermissionPlayer.Update.PERMISSION_BUNDLE_REMOVE,
            key = uuid,
            newValue = name,
            updateCache = true
        )

        NeptunPermissionProvider.api.sendPlayerPermissionChange(uuid)
    }

    override suspend fun addPrivatePermission(uuid: UUID, permission: PermissionInfo) {
        NeptunPermissionProvider.api.permissionPlayerService.bulkUpdateEntry(
            updateType = PermissionPlayer.Update.PRIVATE_PERMISSION_ADD,
            key = uuid,
            newValue = permission,
            updateCache = true
        )

        NeptunPermissionProvider.api.sendPlayerPermissionChange(uuid)
    }

    override suspend fun removePrivatePermission(uuid: UUID, permission: String) {
        NeptunPermissionProvider.api.permissionPlayerService.bulkUpdateEntry(
            updateType = PermissionPlayer.Update.PRIVATE_PERMISSION_REMOVE,
            key = uuid,
            newValue = permission,
            updateCache = true
        )

        NeptunPermissionProvider.api.sendPlayerPermissionChange(uuid)
    }

    override suspend fun getHighestPermissionGroup(permissionPlayer: PermissionPlayer): Pair<PermissionGroup, PermissionGroupInfo>? {
        val groups = mutableMapOf<PermissionGroup, PermissionGroupInfo>()

        for (groupInfo in permissionPlayer.groups) {
            if (groupInfo.isExpired()) continue
            val permissionGroup = groupInfo.toGroup() ?: continue
            groups[permissionGroup] = groupInfo
        }

        return groups.entries.minByOrNull { it.key.priority }?.toPair()
    }

    override suspend fun getPermissionsOfHighestGroup(permissionPlayer: PermissionPlayer): Set<PermissionInfo> {
        val highestGroup = getHighestPermissionGroup(permissionPlayer)?.first
        return highestGroup?.collectAllPermissions() ?: emptySet()
    }

}