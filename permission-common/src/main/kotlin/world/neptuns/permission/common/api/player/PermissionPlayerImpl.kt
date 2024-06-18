package world.neptuns.permission.common.api.player

import kotlinx.coroutines.runBlocking
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.group.PermissionGroupInfo
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.api.player.AuthSession
import world.neptuns.permission.api.player.PermissionPlayer
import java.util.*

class PermissionPlayerImpl(
    override val uuid: UUID,
    override val groups: MutableSet<PermissionGroupInfo>,
    override val privatePermissionBundles: MutableSet<PermissionBundleInfo>,
    override val privatePermissions: MutableSet<PermissionInfo>,
    override var authSession: AuthSession?,
) : PermissionPlayer {

    override suspend fun collectAllPermissions(): Set<PermissionInfo> {
        val allPermissions = NeptunPermissionProvider.api.permissionPlayerAdapter.getPermissionsOfHighestGroup(this).toMutableSet()

        for (permissionBundleInfo in privatePermissionBundles) {
            if (permissionBundleInfo.isExpired()) continue
            val permissionBundle = permissionBundleInfo.toBundle() ?: continue

            for (bundlePermission in permissionBundle.permissions) {
                if (allPermissions.any { it.name == bundlePermission.name }) continue
                allPermissions.add(bundlePermission)
            }
        }

        for (permissionInfo in privatePermissions) {
            if (allPermissions.any { it.name == permissionInfo.name }) continue
            allPermissions.add(permissionInfo)
        }

        return allPermissions
    }

    override suspend fun clearExpiredPrivatePermissions() {
        for (permissionInfo in this.privatePermissions.filter { it.isExpired() }) {
            NeptunPermissionProvider.api.permissionPlayerAdapter.removePrivatePermission(this.uuid, permissionInfo.name)
        }
    }

    override fun hasPermission(permission: String): Boolean = runBlocking {
        val allPermissions = collectAllPermissions()
        allPermissions.any { it.name == permission } || allPermissions.any { it.name == "*" }
    }

    override suspend fun onActiveGroupExpire() {
        val groupData = NeptunPermissionProvider.api.permissionPlayerAdapter.getHighestPermissionGroup(this) ?: return
        val highestGroupInfo = groupData.second

        if (!highestGroupInfo.isExpired()) return

        NeptunPermissionProvider.api.permissionPlayerAdapter.removePermissionGroup(this.uuid, highestGroupInfo.name)
    }

}