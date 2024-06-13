package world.neptuns.permission.common.api.player

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
    override var authSession: AuthSession?
) : PermissionPlayer {

    override fun clearExpiredPrivatePermissions() {
        TODO("Not yet implemented")
    }

    override fun hasPermission(permission: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun onActiveGroupExpire() {
        TODO("Not yet implemented")
    }

}