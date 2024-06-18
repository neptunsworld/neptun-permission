package world.neptuns.permission.common.api.group

import kotlinx.coroutines.runBlocking
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupFormat
import world.neptuns.permission.api.group.PermissionGroupInfo
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo

class PermissionGroupImpl(
    override val name: String,
    override var priority: Int,
    override val hexColorCode: String,
    override val format: PermissionGroupFormat,
    override val permissions: MutableSet<PermissionInfo>,
    override val permissionBundles: MutableSet<PermissionBundleInfo>,
    override val parents: MutableSet<String>,
    override val metadata: MutableMap<String, Any>,
    override var isDefault: Boolean,
) : PermissionGroup {

    override fun collectAllPermissions(): Set<PermissionInfo> = runBlocking {
        val allPermissions = mutableSetOf<PermissionInfo>()

        for (permissionInfo in permissions) {
            if (permissionInfo.isExpired()) continue
            if (allPermissions.any { it.name == permissionInfo.name }) continue
            allPermissions.add(permissionInfo)
        }

        for (permissionBundleInfo in permissionBundles) {
            if (permissionBundleInfo.isExpired()) continue
            val permissionBundle = permissionBundleInfo.toBundle() ?: continue

            for (permission in permissionBundle.permissions) {
                if (allPermissions.any { it.name == permission.name }) continue
                allPermissions.add(permission)
            }
        }

        for (parent in parents) {
            val parentGroup = NeptunPermissionProvider.api.permissionGroupService.getPermissionGroup(parent) ?: continue
            allPermissions.addAll(parentGroup.collectAllPermissions())
        }

        allPermissions
    }

    override suspend fun clearExpiredPermissions() {
        for (permissionInfo in this.permissions.filter { it.isExpired() }) {
            NeptunPermissionProvider.api.permissionGroupAdapter.removePermission(this.name, permissionInfo.name)
        }
    }

    override suspend fun hasPermission(permission: String): Boolean {
        val allPermissions = collectAllPermissions()
        return allPermissions.any { it.name == permission } || allPermissions.any { it.name == "*" }
    }

    override fun toGroupInfo(timeoutTimestamp: Long): PermissionGroupInfo {
        return PermissionGroupInfoImpl(this.name, timeoutTimestamp)
    }

}